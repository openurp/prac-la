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
package org.openurp.prac.la.web.action.admin

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.model.Semester
import org.openurp.prac.la.model.{LaSession, LaVolunteer}
import org.openurp.starter.edu.helper.ProjectSupport

import java.time.LocalDate

class VolunteerAction extends RestfulAction[LaVolunteer] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    val semesterId = getInt("semester.id")
    val semester = {
      semesterId match {
        case None => getCurrentSemester
        case _ => entityDao.get(classOf[Semester], semesterId.get)
      }
    }
    put("project", getProject)
    put("currentSemester", semester)
    put("sessions", entityDao.findBy(classOf[LaSession], "semester", List(semester)))
    super.indexSetting()
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
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
