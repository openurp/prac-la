package org.openurp.edu.la.web.action.admin

import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.la.model.LaSession
import org.beangle.data.dao.OqlBuilder
import org.openurp.edu.base.model.Semester
import java.time.LocalDate
import org.openurp.edu.base.model.Project
import org.beangle.webmvc.api.view.View

class SessionAction extends RestfulAction[LaSession] {
  
  override protected def indexSetting(): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    super.indexSetting()
  }

  override protected def editSetting(entity: LaSession): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    super.editSetting(entity)
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }
  
  
  override protected def saveAndRedirect(session: LaSession): View = {
    val projectId = get("project").get.toInt
    val project = entityDao.get(classOf[Project], projectId)
    session.project = project
    super.saveAndRedirect(session)
  }

}