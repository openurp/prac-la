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

import java.time.{ Instant, LocalDate }

import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.{ Semester, Student }
import org.openurp.edu.boot.web.ProjectSupport
import org.openurp.edu.la.model.{ LaOption, LaSession, LaTaker, Volunteer }
import org.openurp.edu.base.model.Project

class EnrollAction extends RestfulAction[LaTaker] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val project = getProject
    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", project)
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    val volunteers = entityDao.search(volunteerBuilder)
    put("volunteers", volunteers)

    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", project)
    sessionBuilder.where("session.semester=:semester", getCurSemester())
    sessionBuilder.where("session.beginAt<:now and session.endAt>:now", Instant.now())
    val sessions = entityDao.search(sessionBuilder)
    sessions.foreach(session => {
      if (session.grades.contains(students(0).state.get.grade)) {
        put("sessions", sessions)
      }
      put("session", session)
    })
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

  def options(): View = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val project = getProject
    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", project)
    sessionBuilder.where("session.semester=:semester", getCurSemester())
    sessionBuilder.where("session.beginAt<:now and session.endAt>:now", Instant.now())
    val sessions = entityDao.search(sessionBuilder)
    sessions.foreach(session => {
      if (session.grades.contains(students(0).state.get.grade)) {
        put("session", session)
        val takers = getTakers(students(0), project, session)
        val chooseOptions = takers.map(_.option)
        put("chooseOptions", chooseOptions)
      }
    })

    val optionBuilder = OqlBuilder.from(classOf[LaOption], "option")
    optionBuilder.where("option.project=:project", project)
    optionBuilder.where("option.semester=:semester", getCurSemester())
    put("options", entityDao.search(optionBuilder))
    forward()
  }

  private def getVolunteer(std: Student, session: LaSession): Volunteer = {
    val builder = OqlBuilder.from(classOf[Volunteer], "taker")
    builder.where("volunteer.std=:std", std)
    builder.where("Volunteer.updatedAt>:beginAt and Volunteer.updatedAt<:endAt", session.beginAt, session.endAt)
    val rs = entityDao.search(builder)

    val volunteer =
      if (rs.isEmpty) {
        val v = new Volunteer()
        v.std = std
        v.updatedAt = Instant.now
        v
      } else {
        rs.head
      }

    get("adjust").foreach(adjust => {
      adjust match {
        case "是" => volunteer.adjustable = true
        case "否" => volunteer.adjustable = false
      }
    })

    entityDao.saveOrUpdate(volunteer)
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
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val option = entityDao.find(classOf[LaOption], longId("option")).get
    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val volunteer = this.getVolunteer(students(0), session)

    val project = getProject
    val takers = getTakers(students(0), project, session)

    if (option.actual < option.capacity) {
      takers.size match {
        case 0 => {
          val taker = populateEntity()
          taker.rank = 1
          taker.option = option
          taker.updatedAt = Instant.now()
          taker.volunteer = volunteer
          taker.option.actual = taker.option.takers.size + 1
          entityDao.saveOrUpdate(taker.option)
          entityDao.saveOrUpdate(taker)
          redirect("index", "第一志愿报名成功")
        }
        case 1 => {
          val taker = populateEntity()
          taker.rank = 2
          taker.option = option
          taker.volunteer = volunteer
          taker.updatedAt = Instant.now()
          taker.option.actual = taker.option.takers.size + 1
          entityDao.saveOrUpdate(taker.option)
          entityDao.saveOrUpdate(volunteer)
          redirect("index", "第二志愿报名成功")
        }
        case 2 => {
          redirect("index", "两个志愿已满，报名失败")
        }
      }
    } else {
      redirect("index", "所选项目单位报名人数已满，报名失败")
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
