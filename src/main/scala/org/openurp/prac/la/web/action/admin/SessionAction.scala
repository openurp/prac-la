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

import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.{Project, Semester}
import org.openurp.prac.la.model.LaSession
import org.openurp.starter.web.support.ProjectSupport

import java.time.LocalDate

class SessionAction extends RestfulAction[LaSession], ProjectSupport {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    put("currentSemester", getSemester)
    super.indexSetting()
  }

  override protected def editSetting(entity: LaSession): Unit = {
    put("project", getProject)
    val semester = entityDao.get(classOf[Semester], getIntId("laSession.semester"))
    put("semester", semester)
    super.editSetting(entity)
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

  override protected def saveAndRedirect(session: LaSession): View = {
    session.project = getProject
    super.saveAndRedirect(session)
  }

}
