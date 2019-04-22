package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.openurp.edu.base.model.{ Project, Semester }

/**
 * 参见法律援助的企业及其要求
 */
class LaOption extends LongId {
  var project: Project = _
  var semester: Semester = _
  var corporation: Corporation = _
  var request: Option[String] = None
  var benefits: Option[String] = None
  var capacity: Int = _
  var actural: Int = _
}