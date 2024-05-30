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

package org.openurp.prac.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.{InstantRange, Named}
import org.openurp.base.model.{Project, Semester}

/** 开放进行选择的批次
 * 一般一个学期可以开放多次
 */
class LaSession extends LongId with Named with InstantRange {
  var project: Project = _
  var semester: Semester = _
  var grades: String = _
  var minGpa: Float = _
  var noticeUrl: Option[String] = None
  var optionCount: Int = _
  var attachmentRequired: Boolean = _
}
