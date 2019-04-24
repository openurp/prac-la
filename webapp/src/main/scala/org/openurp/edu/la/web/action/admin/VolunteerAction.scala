package org.openurp.edu.la.web.action.admin

import org.beangle.webmvc.api.action.ActionSupport
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.edu.boot.web.ProjectSupport
import org.openurp.edu.la.model.Volunteer
import org.beangle.webmvc.entity.action.RestfulAction
import org.beangle.data.dao.OqlBuilder
import org.openurp.edu.base.model.Project
import org.openurp.edu.base.model.Semester
import java.time.LocalDate

class VolunteerAction extends RestfulAction[Volunteer] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    put("projects", entityDao.getAll(classOf[Project]))
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    super.indexSetting()
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

}