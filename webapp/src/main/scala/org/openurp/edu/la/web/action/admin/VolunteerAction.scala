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
import org.openurp.edu.la.model.LaTaker

class VolunteerAction extends RestfulAction[LaTaker] with ProjectSupport {

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
