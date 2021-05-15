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
package org.openurp.prac.la.model

import scala.collection.mutable.Buffer

import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated
import org.openurp.base.edu.model.{ Semester, Student }



/** 志愿者
  *
  *  记录学生每个轮次的报名数据
  */
class LaVolunteer extends LongId with Updated {

  def this(std: Student) {
    this()
    this.std = std
  }
  /**学生*/
  var std: Student = _

  /**学年学期*/
  var semester: Semester = _

  /**批次*/
  var session: LaSession = _

  /**录取志愿*/
  var enrolledRank: Option[Int] = None

  /**手机*/
  var mobile: String = _

  /**录取单位*/
  var enrolledOption: Option[LaOption] = None

  /**报名记录*/
  var takers: Buffer[LaTaker] = Collections.newBuffer[LaTaker]

  /**绩点*/
  var gpa: Float = _

  def getTaker(rank: Number): Option[LaTaker] = {
    takers.find(_.rank == rank.intValue)
  }

  def rank: Int = {
    enrolledRank.getOrElse(0)
  }
}
