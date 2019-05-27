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
import org.beangle.webmvc.api.view.View
import org.beangle.security.Securities
import java.time.Instant

class VolunteerAction extends RestfulAction[LaTaker] with ProjectSupport {

  override protected def indexSetting(): Unit = {
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

  override def saveAndRedirect(taker: LaTaker): View = {
    val taker = entityDao.get(classOf[LaTaker], longId("laTaker"))
    val volunteer = taker.volunteer

    getBoolean("enrolled") foreach { enrolled =>
      enrolled match {
        case true =>
          if (volunteer.enrolledOption.orNull != taker.option) {
            volunteer.enrolledOption = Some(taker.option)
            volunteer.enrolledRank = Some(taker.rank)
            volunteer.takers foreach { t =>
              if (t.id != taker.id) {
                if (t.enrolled) {
                  t.remark = Some(Securities.user + " 人工取消录取  " + Instant.now.toString)
                }
                t.enrolled = false
              }
            }
            taker.remark = Some(Securities.user + " 人工录取 " + Instant.now.toString)
            taker.enrolled = true
            entityDao.saveOrUpdate(volunteer)
          }
        case false =>
          taker.enrolled = false
          if (volunteer.enrolledOption.orNull == taker.option) {
            volunteer.enrolledOption = None
            volunteer.enrolledRank = None
            taker.remark = Some(Securities.user + " 人工取消录取 " + Instant.now.toString)
          }
          entityDao.saveOrUpdate(volunteer)
      }
    }
    super.saveAndRedirect(taker)
  }
}
