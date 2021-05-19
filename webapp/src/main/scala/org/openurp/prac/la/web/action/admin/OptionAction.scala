/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.prac.la.web.action.admin

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.{Collections, Order}
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.annotation.ignore
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.model.Semester
import org.openurp.prac.la.model.{LaCorporation, LaOption, LaSession}
import org.openurp.starter.edu.helper.ProjectSupport

import java.time.LocalDate

class OptionAction extends RestfulAction[LaOption] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    val semesterId = getInt("semester.id")
    val semester = {
      semesterId match {
        case None => getCurrentSemester
        case _ => entityDao.get(classOf[Semester], semesterId.get)
      }
    }
    put("project", getProject)
    put("currentSemester", semester)
    put("sessions", entityDao.findBy(classOf[LaSession], "semester", List(semester)))
    super.indexSetting()
  }

  override protected def editSetting(entity: LaOption): Unit = {
    val semester = entityDao.get(classOf[Semester], intId("laOption.semester"))
    put("semester", semester)
    val sessions = entityDao.findBy(classOf[LaSession], "semester", List(semester))
    put("sessions", sessions)
    val coQuery = OqlBuilder.from(classOf[LaCorporation], "co")
    getLong("laOption.session.id") foreach { sessionId =>
      coQuery.where("not exists(from " + classOf[LaOption].getName +
        " lo where lo.corporation=co and lo.session.id=:sessionId)", sessionId)
      coQuery.orderBy("co.name")
    }
    val corporations = entityDao.search(coQuery)

    if (null != entity.corporation && entity.corporation.persisted && !corporations.contains(entity.corporation)) {
      val c = corporations.toBuffer
      c += entity.corporation
      put("corporations", c)
    } else {
      put("corporations", corporations)
    }
    super.editSetting(entity)
  }

  def report(): View = {
    val options = entityDao.find(classOf[LaOption], longIds("laOption"))
    put("options", options)
    forward()
  }

  def autoEnroll(): View = {
    val session = entityDao.get(classOf[LaSession], longId("laOption.session"))
    val builder = OqlBuilder.from(classOf[LaOption], "laOption")
    builder.where("laOption.project=:project", getProject)
    builder.where("laOption.semester=:semester", session.semester)
    builder.where("laOption.session=:session", session)
    builder.where("size(laOption.volunteers) < laOption.capacity")
    val options = entityDao.search(builder)

    //自动录取第一志愿
    options foreach (enrollRank(_, 1))
    options foreach (enrollRank(_, 2))
    redirect("search", "info.save.success")
  }

  private def enrollRank(option: LaOption, rank: Int): Unit = {
    val remained = option.capacity - option.volunteers.size
    if (remained > 0) {
      val volunteerStds = option.volunteers.map(_.std).toSet
      var takers = option.takers.filter(x => !volunteerStds.contains(x.volunteer.std) && x.volunteer.enrolledOption.isEmpty && x.rank == rank)
      takers = takers.sorted(new MultiPropertyOrdering("rank,volunteer.gpa desc,updatedAt"))
      val enrolled = takers.take(remained)
      //println(s"takers size ${takers.size} and enroll $remained for ${option.corporation.name},enroll actual ${enrolled.size}");
      enrolled foreach { taker =>
        taker.enrolled = true
        taker.volunteer.enrolledOption = Some(taker.option)
        taker.volunteer.enrolledRank = Some(taker.rank)
      }
      val volunteers = enrolled.map(_.volunteer)
      option.volunteers ++= volunteers
      entityDao.saveOrUpdate(volunteers)
    }
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

  @ignore
  protected override def removeAndRedirect(entities: Seq[LaOption]): View = {
    println("remove entities :" + entities.size)
    remove(entities)
    redirect("search", "info.remove.success")
  }

  override protected def getQueryBuilder(): OqlBuilder[LaOption] = {
    val builder = OqlBuilder.from(classOf[LaOption], "laOption")
    get("signup_status").foreach(a =>
      a match {
        case "0" => builder.where("size(laOption.takers)<option.capacity")
        case "1" => builder.where("size(laOption.takers)>=option.capacity")
        case _ =>
      })

    get("enroll_status").foreach(a =>
      a match {
        case "0" => builder.where("size(laOption.volunteers)<option.capacity")
        case "1" => builder.where("size(laOption.volunteers)>=option.capacity")
        case _ =>
      })
    populateConditions(builder)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
  }

  def corporation(): View = {
    val name = get("term").orNull
    val query = OqlBuilder.from(classOf[LaCorporation], "corporation")
    populateConditions(query)

    if (Strings.isNotEmpty(name)) {
      query.where("corporation.name like :name ", '%' + name + '%')
    }
    val pageLimit = getPageLimit
    query.limit(pageLimit);
    put("corporations", entityDao.search(query))
    put("pageLimit", pageLimit)
    forward()
  }

  override protected def saveAndRedirect(option: LaOption): View = {
    option.project = getProject
    super.saveAndRedirect(option)
  }

  def takers(): View = {
    val optionId = longId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    var takers = Collections.newBuffer(option.takers)
    takers = takers.sorted(new MultiPropertyOrdering("rank,volunteer.gpa desc,updatedAt"))
    put("laOption", option)
    put("takers", takers)
    forward()
  }

  def volunteers(): View = {
    val optionId = longId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    put("laOption", option)
    forward()
  }

}
