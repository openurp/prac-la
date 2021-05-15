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
import org.beangle.security.Securities
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.model.Semester
import org.openurp.boot.edu.helper.ProjectSupport
import org.openurp.prac.la.model.{LaSession, LaTaker}

import java.time.{Instant, LocalDate}

class VolunteerAction extends RestfulAction[LaTaker] with ProjectSupport {

  override protected def indexSetting(): Unit = {
    val semesterId = getInt("semester.id")
    val semester = {
      semesterId match {
        case None => getCurrentSemester
        case _ => entityDao.get(classOf[Semester], semesterId.get)
      }
    }
    put("project",getProject)
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
