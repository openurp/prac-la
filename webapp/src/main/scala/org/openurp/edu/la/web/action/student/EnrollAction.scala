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
package org.openurp.edu.la.web.action.student

import java.time.{Instant, LocalDate}

import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.{Semester, Student}
import org.openurp.edu.boot.web.ProjectSupport
import org.openurp.edu.la.model.{LaOption, LaSession, LaTaker, Volunteer}
import org.openurp.edu.base.model.Project
import org.beangle.data.model.Entity
import org.beangle.commons.collection.Collections
import javax.sql.DataSource
import org.beangle.data.jdbc.query.JdbcExecutor

class EnrollAction extends RestfulAction[LaTaker] with ProjectSupport {

  var eamsDataSource: DataSource = _

  override protected def indexSetting(): Unit = {
    val project = getProject
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    stdBuilder.where("student.project =:project ", project)

    val student = entityDao.search(stdBuilder).head

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
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
    optionBuilder.where("option.semester=:semester", getCurSemester())
    optionBuilder.where("option.enrolled < option.enrollLimit and option.actual < option.capacity")

    put("options", entityDao.search(optionBuilder))
    put("student", student)
    put("session", session)
    put("volunteer", volunteer)
    forward()
  }

  private def getVolunteer(std: Student, session: LaSession): Volunteer = {
    val volunteer = populateEntity(classOf[Volunteer], "volunteer")
    if (null == volunteer.std) {
      volunteer.std = std
      volunteer.semester = session.semester
    } else {
      if (volunteer.std != std) {
        throw new RuntimeException("Cannot recognize volunteer id:" + volunteer.id + " for user " + std.user.code)
      }
    }
    if (null == volunteer.updatedAt) {
      volunteer.updatedAt = Instant.now
    }
    volunteer
  }

  private def getTakers(std: Student, project: Project, session: LaSession): Seq[LaTaker] = {
    val takerBuilder = OqlBuilder.from(classOf[LaTaker], "taker")
    takerBuilder.where("taker.volunteer.std=:std", std)
    takerBuilder.where("taker.option.project=:project", project)
    takerBuilder.where("taker.option.semester=:semester", getCurSemester())
    takerBuilder.where("taker.updatedAt>:beginAt and taker.updatedAt<:endAt", session.beginAt, session.endAt)
    entityDao.search(takerBuilder)
  }

  protected def choose(): View = {
    val student = getStudent
    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val volunteer = getVolunteer(student, session)
    val project = getProject
    val saved = Collections.newBuffer[Entity[_]]
    saved += volunteer

    var selected = 0
    var firstSuccess = false
    (1 to 2) foreach { rank =>
      get("option" + rank, classOf[Long]) match {
        case Some(id) =>
          selected += 1
          if (rank == 1 || firstSuccess) {
            val option = entityDao.get(classOf[LaOption], id)
            if (option.actual < option.capacity) {
              volunteer.getTaker(rank) match {
                case Some(t) =>
                  if (t.option != option) {
                    t.option.actual -= 1
                    t.option.takers -= t
                    saved += t.option
                    t.option = option
                    option.actual += 1
                    option.takers += t
                  }
                  t
                case None =>
                  val nt = new LaTaker(volunteer, rank, option)
                  volunteer.takers += nt
                  option.actual += 1
                  option.takers += nt
                  nt
              }
              saved += option
            }
            if (rank == 1) {
              firstSuccess = volunteer.getTaker(1).isDefined
            }
          }
        case None =>
          volunteer.getTaker(rank) foreach { taker =>
            volunteer.takers -= taker
            taker.option.actual -= 1
            taker.option.takers -= taker
            saved += taker.option
          }
      }
    }

    if (selected == volunteer.takers.size) {
      entityDao.saveOrUpdate(saved)
      redirect("index", "报名成功")
    } else {
      if (volunteer.takers.size > 0) {
        entityDao.saveOrUpdate(saved)
        redirect("index", "第一志愿报名成功，第二志愿人数已满!")
      } else {
        redirect("index", "两个志愿人数已满!")
      }
    }
  }

  protected def unChoose(): View = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val session = entityDao.get(classOf[LaSession], longId("session"))
    val volunteer = this.getVolunteer(students(0), session)
    val takers = getTakers(students(0), getProject, session)
    takers.size match {
      case 1 => {
        val taker = takers.head
        entityDao.remove(takers)
        taker.option.actual = taker.option.takers.size
        entityDao.saveOrUpdate(taker.option)
      }
      case 2 => {
        val taker = takers.head
        if (takers(0) == volunteer) {
          takers(1).rank = 1
        } else {
          takers(0).rank = 1
        }
        entityDao.remove(volunteer)
        taker.option.actual = taker.option.takers.size
        entityDao.saveOrUpdate(taker.option)
      }
    }
    redirect("index")
  }

  protected def changeNo(): View = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val takers = getTakers(students(0), getProject, session)
    takers.size match {
      case 2 => {
        if (takers(0).rank == 1) {
          takers(0).rank = 2
          takers(1).rank = 1
        } else {
          takers(0).rank = 1
          takers(1).rank = 2
        }
        saveOrUpdate(takers)
      }
      case _ =>
    }
    redirect("index")
  }

}
