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
package org.openurp.prac.la.web.action.student

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.jdbc.query.JdbcExecutor
import org.beangle.data.model.Entity
import org.beangle.security.Securities
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.model.{Project, Semester, Student}
import org.openurp.boot.edu.helper.ProjectSupport
import org.openurp.prac.la.model.{LaOption, LaSession, LaTaker, LaVolunteer}

import java.time.{Instant, LocalDate}
import javax.sql.DataSource

class EnrollAction extends RestfulAction[LaTaker] with ProjectSupport {

  var eamsDataSource: DataSource = _

  override protected def indexSetting(): Unit = {
    val project = getProject
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    stdBuilder.where("student.project =:project ", project)

    val student = entityDao.search(stdBuilder).head

    val volunteerBuilder = OqlBuilder.from(classOf[LaVolunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", student)
    volunteerBuilder.where("volunteer.semester=:semester", getCurSemester())
    val volunteers = entityDao.search(volunteerBuilder)
    put("volunteers", volunteers)

    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", project)
    sessionBuilder.where("session.semester=:semester", getCurSemester())
    sessionBuilder.where("session.beginAt<:now and session.endAt>:now", Instant.now())
    val sessions = entityDao.search(sessionBuilder)
      .filter { s => s.grades.contains(student.state.get.grade) }
    put("sessions", sessions)
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

  def signup(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val project = getProject
    val volunteer = getVolunteer(student, session)

    val gpa: Option[Number] = new JdbcExecutor(eamsDataSource)
      .unique("select gpa from eams.CJ_JDTJ_T where XSID=?", student.id)

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
    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val volunteer = getVolunteer(student, session)
    volunteer.updatedAt = Instant.now
    volunteer.mobile = get("volunteer.mobile").get
    volunteer.gpa = getFloat("volunteer.gpa").get

    val project = getProject
    val saved = Collections.newBuffer[Entity[_]]
    saved += volunteer

    (1 to 2) foreach { rank =>
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

    entityDao.saveOrUpdate(saved)
    redirect("index", "报名成功")
  }

  protected def cancel(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], longId("session")).get
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

  private def getStudent: Student = {
    val query = OqlBuilder.from(classOf[Student], "s")
    query.where("s.user.code=:code", Securities.user)
    entityDao.search(query).head
  }

}
