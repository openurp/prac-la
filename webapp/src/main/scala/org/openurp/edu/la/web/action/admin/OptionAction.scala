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
package org.openurp.edu.la.web.action.admin

import java.time.LocalDate

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.{Collections, Order}
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.Semester
import org.openurp.edu.boot.web.ProjectSupport
import org.openurp.edu.la.model.{Corporation, LaOption}
import org.openurp.edu.la.model.LaSession
import org.beangle.webmvc.api.annotation.ignore

class OptionAction extends RestfulAction[LaOption] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    put("sessions", entityDao.getAll(classOf[LaSession]))
    super.indexSetting()
  }

  override protected def editSetting(entity: LaOption): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    val semester = getCurSemester()
    put("currentSemester", semester)
    put("sessions", entityDao.getAll(classOf[LaSession]))
    val coQuery = OqlBuilder.from(classOf[Corporation], "co")
    getLong("option.session.id") foreach { sessionId =>
      coQuery.where("not exists(from " + classOf[LaOption].getName +
        " lo where lo.corporation=co and lo.session.id=:sessionId)", sessionId)
      coQuery.orderBy("co.name")
    }
    var corporations = entityDao.search(coQuery)

    if (null != entity.corporation && !corporations.contains(entity.corporation)) {
      val c = corporations.toBuffer
      c += entity.corporation
      put("corporations", c)
    } else {
      put("corporations", corporations)
    }
    super.editSetting(entity)
  }

  def report(): View = {
    val options = entityDao.find(classOf[LaOption], longIds("option"))
    put("options", options)
    forward()
  }

  def autoEnroll(): View = {
    val semester = entityDao.get(classOf[Semester], intId("option.semester"))
    val session = entityDao.get(classOf[LaSession], longId("option.session"))
    val builder = OqlBuilder.from(classOf[LaOption], "option")
    builder.where("option.project=:project", getProject)
    builder.where("option.semester=:semester", semester)
    builder.where("option.session=:session", session)
    builder.where("size(option.volunteers) < option.capacity")
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
    val builder = OqlBuilder.from(classOf[LaOption], "option")
    get("signup_status").foreach(a =>
      a match {
        case "0" => builder.where("size(option.takers)<option.capacity")
        case "1" => builder.where("size(option.takers)>=option.capacity")
        case _   =>
      })

    get("enroll_status").foreach(a =>
      a match {
        case "0" => builder.where("size(option.volunteers)<option.capacity")
        case "1" => builder.where("size(option.volunteers)>=option.capacity")
        case _   =>
      })
    populateConditions(builder)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
  }

  def corporation(): View = {
    val name = get("term").orNull
    val query = OqlBuilder.from(classOf[Corporation], "corporation")
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
    val corporationId = longId("laOption.corporation")
    val corporation = entityDao.get(classOf[Corporation], corporationId)
    super.saveAndRedirect(option)
  }

  def takers(): View = {
    val optionId = longId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    var takers = Collections.newBuffer(option.takers)
    takers = takers.sorted(new MultiPropertyOrdering("rank,volunteer.gpa desc,updatedAt"))
    put("option", option)
    put("takers", takers)
    forward()
  }

  def volunteers(): View = {
    val optionId = longId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    put("option", option)
    forward()
  }

}
