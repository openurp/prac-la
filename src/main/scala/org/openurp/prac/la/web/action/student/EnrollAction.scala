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

package org.openurp.prac.la.web.action.student

import jakarta.servlet.http.Part
import org.beangle.commons.bean.Initializing
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.model.Entity
import org.beangle.ems.app.EmsApp
import org.beangle.ems.app.datasource.AppDataSourceFactory
import org.beangle.jdbc.query.JdbcExecutor
import org.beangle.web.action.view.View
import org.openurp.base.model.Project
import org.openurp.base.std.model.Student
import org.openurp.prac.la.model.{LaOption, LaSession, LaTaker, LaVolunteer}
import org.openurp.starter.web.support.StudentSupport

import java.time.Instant
import javax.sql.DataSource

/** 报名
 */
class EnrollAction extends StudentSupport, Initializing {

  var eamsDataSource: DataSource = _

  override def init(): Unit = {
    val ds = new AppDataSourceFactory()
    ds.name = "eams"
    ds.init()
    eamsDataSource = ds.result
  }

  protected override def projectIndex(student: Student): View = {
    given project: Project = student.project

    val volunteerBuilder = OqlBuilder.from(classOf[LaVolunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", student)
    volunteerBuilder.where("volunteer.semester=:semester", getSemester)
    val volunteers = entityDao.search(volunteerBuilder)
    put("volunteers", volunteers)

    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", project)
    sessionBuilder.where("session.semester=:semester", getSemester)
    sessionBuilder.where("session.beginAt<:now and session.endAt>:now", Instant.now())
    val sessions = entityDao.search(sessionBuilder).filter { s => s.grades.contains(student.state.get.grade.code) }
    put("sessions", sessions)
    forward()
  }

  def signup(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], getLongId("session")).get
    val project = getProject
    val volunteer = getVolunteer(student, session)

    val gpa: Option[Number] = new JdbcExecutor(eamsDataSource)
      .unique("select gpa from edu.std_gpas where std_id=?", student.id)

    volunteer.gpa = gpa.map(_.floatValue).getOrElse(0)
    if (java.lang.Float.compare(volunteer.gpa, session.minGpa) < 0) {
      put("gpa", volunteer.gpa)
      put("session", session)
      return forward("lowgpa")
    }
    val takers = getTakers(student, project, session)
    val chooseOptions = takers.map(_.option)
    put("chooseOptions", chooseOptions)

    val optionBuilder = OqlBuilder.from(classOf[LaOption], "option")
    optionBuilder.where("option.project=:project", project)
    optionBuilder.where("option.session=:session", session)
    optionBuilder.where("size(option.volunteers) < option.capacity")

    put("options", entityDao.search(optionBuilder))
    put("student", student)
    put("session", session)
    put("volunteer", volunteer)
    forward()
  }

  private def getVolunteer(std: Student, session: LaSession): LaVolunteer = {
    val builder = OqlBuilder.from(classOf[LaVolunteer], "volunteer")
    builder.where("volunteer.std=:std", std)
    builder.where("volunteer.session=:session", session)

    val volunteers = entityDao.search(builder)
    var volunteer: LaVolunteer = null
    if (volunteers.isEmpty) {
      volunteer = new LaVolunteer();
      volunteer.std = std
      volunteer.semester = session.semester
      volunteer.session = session
      volunteer.updatedAt = Instant.now
    } else {
      volunteer = volunteers.head
    }
    volunteer
  }

  private def getTakers(std: Student, project: Project, session: LaSession): Seq[LaTaker] = {
    val takerBuilder = OqlBuilder.from(classOf[LaTaker], "taker")
    takerBuilder.where("taker.volunteer.std=:std", std)
    takerBuilder.where("taker.option.project=:project", project)
    takerBuilder.where("taker.option.session=:session", session)
    entityDao.search(takerBuilder)
  }

  protected def choose(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], getLongId("session")).get
    val volunteer = getVolunteer(student, session)
    volunteer.updatedAt = Instant.now
    volunteer.mobile = get("volunteer.mobile").get
    volunteer.gpa = getFloat("volunteer.gpa").get

    val project = getProject
    val saved = Collections.newBuffer[Entity[_]]
    saved += volunteer

    (1 to session.optionCount) foreach { rank =>
      get("option" + rank, classOf[Long]) match {
        case Some(id) =>
          val option = entityDao.get(classOf[LaOption], id)
          volunteer.getTaker(rank) match {
            case Some(t) =>
              if (t.option != option) {
                t.option.takers -= t
                saved += t.option
                t.option = option
                option.takers += t
              }
              t.updatedAt = Instant.now
            case None =>
              val nt = new LaTaker(volunteer, rank, option)
              volunteer.takers += nt
              option.takers += nt
          }
          saved += option
        case None =>
          volunteer.getTaker(rank) foreach { taker =>
            volunteer.takers -= taker
            taker.option.takers -= taker
            saved += taker.option
          }
      }
    }
    val parts = getAll("attachment", classOf[Part])
    if (parts.nonEmpty && parts.head.getSize > 0) {
      val part = parts.head
      val blob = EmsApp.getBlobRepository(true)
      val storeName = s"${student.code} 简历." + Strings.substringAfterLast(part.getSubmittedFileName, ".")

      val existPath = volunteer.attachmentPath.orNull
      if (null != existPath && existPath.startsWith("/")) blob.remove(existPath)
      val meta = blob.upload("/la/" + session.id.toString + "/", part.getInputStream, storeName, student.code + " " + student.name)
      volunteer.attachmentPath = Some(meta.filePath)
    }
    entityDao.saveOrUpdate(saved)
    redirect("index", "报名成功")
  }

  protected def cancel(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], getLongId("session")).get
    val volunteer = getVolunteer(student, session)
    if (volunteer.enrolledOption.isEmpty) {
      val takers = getTakers(student, getProject, session)
      entityDao.remove(takers)
      entityDao.remove(volunteer)
      redirect("index", "取消成功")
    } else {
      redirect("index", "已经确定面试，暂不能取消")
    }
  }

}
