package org.openurp.edu.la.web.action.student

import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.la.model.Volunteer
import org.openurp.edu.la.model.LaOption
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.openurp.edu.base.model.Student
import org.openurp.edu.la.model.LaSession
import org.openurp.edu.base.model.Project
import org.openurp.edu.base.model.Semester
import java.time.LocalDate
import java.time.Instant
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.api.annotation.mapping
import org.openurp.edu.boot.web.ProjectSupport

class EnrollAction extends RestfulAction[Volunteer] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", getProject())
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    val volunteers = entityDao.search(volunteerBuilder)
    put("volunteers", volunteers)

    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", getProject())
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

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", getProject())
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    val volunteers = entityDao.search(volunteerBuilder)
    val chooseOptions = volunteers.map(_.option)
    put("chooseOptions", chooseOptions)

    val sessionBuilder = OqlBuilder.from(classOf[LaSession], "session")
    sessionBuilder.where("session.project=:project", getProject())
    sessionBuilder.where("session.semester=:semester", getCurSemester())
    sessionBuilder.where("session.beginAt<:now and session.endAt>:now", Instant.now())
    val sessions = entityDao.search(sessionBuilder)
    sessions.foreach(session => {
      if (session.grades.contains(students(0).state.get.grade)) {
        put("sessions", sessions)
      }
      put("session", session)
    })

    val optionBuilder = OqlBuilder.from(classOf[LaOption], "option")
    optionBuilder.where("option.project=:project", getProject())
    optionBuilder.where("option.semester=:semester", getCurSemester())
    put("options", entityDao.search(optionBuilder))
    forward()
  }

  protected def choose(): View = {
    val user = Securities.user
    val stdBuilder = OqlBuilder.from(classOf[Student], "student")
    stdBuilder.where("student.user.code =:code ", user)
    val students = entityDao.search(stdBuilder)

    val option = entityDao.find(classOf[LaOption], longId("option")).get
    val session = entityDao.find(classOf[LaSession], longId("session")).get
    val adjust = get("adjust")

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", getProject())
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    volunteerBuilder.where("volunteer.updatedAt>:beginAt and volunteer.updatedAt<:endAt", session.beginAt, session.endAt)
    val volunteers = entityDao.search(volunteerBuilder)

    if (option.actual < option.capacity) {
      volunteers.size match {
        case 0 => {
          val volunteer = populateEntity()
          volunteer.indexno = 1
          volunteer.option = option
          volunteer.std = students(0)
          volunteer.updatedAt = Instant.now()
          get("adjust").foreach(adjust => {
            adjust match {
              case "是" => volunteer.adjust = true
              case "否" => volunteer.adjust = false
            }
          })
          volunteer.option.actual = volunteer.option.volunteers.size + 1
          entityDao.saveOrUpdate(volunteer.option)
          entityDao.saveOrUpdate(volunteer)
          redirect("index", "第一志愿报名成功")
        }
        case 1 => {
          val volunteer = populateEntity()
          volunteer.indexno = 2
          volunteer.option = option
          volunteer.std = students(0)
          volunteer.updatedAt = Instant.now()
          get("adjust").foreach(adjust => {
            adjust match {
              case "是" => volunteer.adjust = true
              case "否" => volunteer.adjust = false
            }
          })
          volunteer.option.actual = volunteer.option.volunteers.size + 1
          entityDao.saveOrUpdate(volunteer.option)
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

    val session = entityDao.find(classOf[LaSession], longId("session")).get

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", getProject())
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    volunteerBuilder.where("volunteer.updatedAt>:beginAt and volunteer.updatedAt<:endAt", session.beginAt, session.endAt)
    val volunteers = entityDao.search(volunteerBuilder)

    val volunteer = entityDao.get(classOf[Volunteer], longId("volunteer"))
    volunteers.size match {
      case 1 => {
        entityDao.remove(volunteer)
        volunteer.option.actual = volunteer.option.volunteers.size
        entityDao.saveOrUpdate(volunteer.option)
      }
      case 2 => {
        if (volunteers(0) == volunteer) {
          volunteers(1).indexno = 1
        } else {
          volunteers(0).indexno = 1
        }
        entityDao.remove(volunteer)
        volunteer.option.actual = volunteer.option.volunteers.size
        entityDao.saveOrUpdate(volunteer.option)
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

    val volunteerBuilder = OqlBuilder.from(classOf[Volunteer], "volunteer")
    volunteerBuilder.where("volunteer.std=:std", students(0))
    volunteerBuilder.where("volunteer.option.project=:project", getProject())
    volunteerBuilder.where("volunteer.option.semester=:semester", getCurSemester())
    volunteerBuilder.where("volunteer.updatedAt>:beginAt and volunteer.updatedAt<:endAt", session.beginAt, session.endAt)
    val volunteers = entityDao.search(volunteerBuilder)
    volunteers.size match {
      case 2 => {
        if (volunteers(0).indexno == 1) {
          volunteers(0).indexno = 2
          volunteers(1).indexno = 1
        } else {
          volunteers(0).indexno = 1
          volunteers(1).indexno = 2
        }
        saveOrUpdate(volunteers)
      }
      case _ =>
    }
    redirect("index")
  }

}