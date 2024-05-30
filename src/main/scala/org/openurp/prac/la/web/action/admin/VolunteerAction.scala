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
import org.beangle.ems.app.EmsApp
import org.beangle.web.action.context.ActionContext
import org.beangle.web.action.view.{Status, View}
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.{Project, Semester}
import org.openurp.prac.la.model.{LaSession, LaVolunteer}
import org.openurp.starter.web.support.ProjectSupport

class VolunteerAction extends RestfulAction[LaVolunteer], ProjectSupport {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("project", project)
    val semester = getSemester
    put("currentSemester", semester)
    put("sessions", entityDao.findBy(classOf[LaSession], "semester", List(semester)))
    super.indexSetting()
  }

  def download(): View = {
    val volunteer = entityDao.get(classOf[LaVolunteer], getLongId("laVolunteer"))
    volunteer.attachmentPath match
      case None => Status.NotFound
      case Some(path) =>
        val url = EmsApp.getBlobRepository(true).url(path)
        val response = ActionContext.current.response
        response.sendRedirect(url.get.toString)
        null
  }

  override protected def getQueryBuilder: OqlBuilder[LaVolunteer] = {
    val builder = super.getQueryBuilder
    getBoolean("enrolled") foreach { enrolled =>
      if (enrolled) {
        builder.where("laVolunteer.enrolledRank is not null")
      } else {
        builder.where("laVolunteer.enrolledRank is null")
      }
    }
    builder
  }
}
