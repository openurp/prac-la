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
package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated
import org.openurp.edu.base.model.Student
import org.openurp.edu.base.model.Semester
import org.beangle.commons.collection.Collections
import scala.collection.mutable.Buffer

/** 志愿者
  *
  *  记录学生每个轮次的报名数据
  */
class Volunteer extends LongId with Updated {

  /**学生*/
  var std: Student = _

  /**学年学期*/
  var semester: Semester = _

  /**是否可调剂*/
  var adjustable: Boolean = _

  /**录入志愿*/
  var enrolledRank: Option[Int] = None

  /**手机*/
  var mobile: String = _

  /**录取单位*/
  var enrolledOption: LaOption = _

  /**是否调剂录取*/
  var adjustEnrolled: Boolean = _

  /**报名记录*/
  var takers: Buffer[LaTaker] = Collections.newBuffer[LaTaker]

  /**绩点*/
  var gpa: Float = _
}
