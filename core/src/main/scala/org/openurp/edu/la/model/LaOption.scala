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

import scala.collection.mutable.Buffer

import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.openurp.edu.base.model.{Project, Semester}
import org.beangle.data.model.pojo.Remark

/** 参见法律援助的企业及其要求
  */
class LaOption extends LongId with Remark {
  var project: Project = _
  var semester: Semester = _
  var corporation: Corporation = _
  var requirement: Option[String] = None

  /**实际录取*/
  var enrolled: Int = _

  /**拟录取*/
  var enrollLimit: Int = _

  /**报名人数上限*/
  var capacity: Int = _

  /**实际报名人数*/
  var actual: Int = _

  /**报名列表*/
  var takers: Buffer[LaTaker] = Collections.newBuffer[LaTaker]
}
