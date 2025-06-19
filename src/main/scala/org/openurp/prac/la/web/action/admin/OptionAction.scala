/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.prac.la.web.action.admin

import org.beangle.commons.bean.orderings.PropertyOrdering
import org.beangle.commons.collection.{Collections, Order}
import org.beangle.commons.concurrent.Workers
import org.beangle.commons.file.zip.Zipper
import org.beangle.commons.io.Files
import org.beangle.commons.lang.Strings
import org.beangle.commons.net.http.HttpUtils
import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.ems.app.EmsApp
import org.beangle.webmvc.annotation.ignore
import org.beangle.webmvc.view.{Status, Stream, View}
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.std.model.StudentState
import org.openurp.prac.la.model.*
import org.openurp.starter.web.support.ProjectSupport
import org.beangle.commons.activation.MediaTypes

import java.io.File

class OptionAction extends RestfulAction[LaOption], ProjectSupport, ExportSupport[LaOption] {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    val semester = getSemester
    put("currentSemester", getSemester)
    put("sessions", entityDao.findBy(classOf[LaSession], "semester", List(semester)))
    super.indexSetting()
  }

  override protected def editSetting(entity: LaOption): Unit = {
    val semester = entityDao.get(classOf[Semester], getIntId("laOption.semester"))
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
    val options = entityDao.find(classOf[LaOption], getLongIds("laOption"))
    put("options", options)
    forward()
  }

  def autoEnroll(): View = {
    val session = entityDao.get(classOf[LaSession], getLongId("laOption.session"))
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
      takers = takers.sorted(PropertyOrdering.by("rank,volunteer.gpa desc,updatedAt"))
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

  @ignore
  protected override def removeAndRedirect(entities: Seq[LaOption]): View = {
    remove(entities)
    redirect("search", "info.remove.success")
  }

  override protected def getQueryBuilder: OqlBuilder[LaOption] = {
    val builder = OqlBuilder.from(classOf[LaOption], "laOption")
    get("signup_status").foreach {
      case "0" => builder.where("size(laOption.takers)<option.capacity")
      case "1" => builder.where("size(laOption.takers)>=option.capacity")
      case _ =>
    }

    get("enroll_status").foreach {
      case "0" => builder.where("size(laOption.volunteers)<option.capacity")
      case "1" => builder.where("size(laOption.volunteers)>=option.capacity")
      case _ =>
    }
    populateConditions(builder)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
  }

  def corporation(): View = {
    val name = get("term").orNull
    val query = OqlBuilder.from(classOf[LaCorporation], "corporation")
    populateConditions(query)

    if (Strings.isNotEmpty(name)) {
      query.where("corporation.name like :name ", s"%${name}%")
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
    val optionId = getLongId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    var takers = Collections.newBuffer(option.takers)
    takers = takers.sorted(PropertyOrdering.by("rank,volunteer.gpa desc,updatedAt"))
    put("laOption", option)
    put("takers", takers)
    forward()
  }

  def volunteers(): View = {
    val optionId = getLongId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    put("laOption", option)
    forward()
  }

  def download(): View = {
    val option = entityDao.get(classOf[LaOption], getLongId("laOption"))
    val dir = new File(System.getProperty("java.io.tmpdir") + s"option${option.id}" + Files./)
    if dir.exists() then Files.travel(dir, f => f.delete())
    dir.mkdirs()
    val innerFiles = Collections.newBuffer[File]
    val blob = EmsApp.getBlobRepository(true)
    Workers.work(option.takers, (t: LaTaker) => {
      t.volunteer.attachmentPath foreach { path =>
        val std = t.volunteer.std
        val stdName = StdNamePurifier.purify(std.name)
        blob.url(path) foreach { url =>
          val fileName = std.code + "_" + stdName + "." + Strings.substringAfterLast(path, ".")
          val localFile = new File(dir.getAbsolutePath + Files./ + fileName)
          HttpUtils.download(url.openConnection(), localFile)
          if (localFile.exists()) innerFiles.addOne(localFile)
        }
      }
    }, Runtime.getRuntime.availableProcessors)
    if (innerFiles.isEmpty) {
      Status.NotFound
    } else {
      val zipFile = new File(dir.getAbsolutePath + Files./ + s"${option.corporation.name}.zip")
      Zipper.zip(dir, innerFiles, zipFile, "utf-8")
      Stream(zipFile, MediaTypes.ApplicationZip, s"${option.corporation.name}报名材料.zip").cleanup { () =>
        Files.travel(dir, f => f.delete())
        dir.delete()
      }
    }
  }

  @ignore
  override def configExport(context: ExportContext): Unit = {
    val optionId = getLongId("laOption")
    val option = entityDao.get(classOf[LaOption], optionId)
    val volunteers = option.orderedVolunteers
    context.put("option", option)
    context.put("volunteers", volunteers)
    context.put("remark", option.remark.getOrElse(""))
    context.put("requirement", option.requirement.getOrElse(""))
    val states = new java.util.HashMap[Long, StudentState]
    val indexes = new java.util.HashMap[Long, Int]
    var i = 1
    volunteers foreach { v =>
      states.put(v.std.id, v.std.state.get)
      indexes.put(v.std.id, i)
      i += 1
    }
    context.put("states", states)
    context.put("indexes", indexes)
    context.fileName = option.semester.schoolYear + "年度" + option.corporation.name + "面试名单.xls"
  }
}
