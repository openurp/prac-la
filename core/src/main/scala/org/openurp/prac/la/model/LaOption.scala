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

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Remark
import org.openurp.base.edu.model.{Project, Semester}

/** 参见法律援助的企业及其要求
  */
class LaOption extends LongId with Remark {
  var project: Project = _

  /**学年学期*/
  var semester: Semester = _

  /**报名批次*/
  var session: LaSession = _

  /**报名单位*/
  var corporation: LaCorporation = _

  /**要求*/
  var requirement: Option[String] = None

  /**拟录取人数*/
  var enrollLimit: Int = _

  /**面试人数上限*/
  var capacity: Int = _

  /**报名列表*/
  var takers: Buffer[LaTaker] = Collections.newBuffer[LaTaker]

  /**实际面试名单*/
  var volunteers: Buffer[LaVolunteer] = Collections.newBuffer[LaVolunteer]

  def orderedVolunteers: Buffer[LaVolunteer] = {
    val a = Collections.newBuffer(volunteers)
    a.sorted(new MultiPropertyOrdering("rank,gpa desc,updatedAt"))
  }
}
