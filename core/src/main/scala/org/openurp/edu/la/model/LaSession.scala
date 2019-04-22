package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.{ InstantRange, Named }
import org.openurp.edu.base.model.Project
import org.openurp.edu.base.model.Semester

/**
 * 开放进行选择的批次
 * 一般一个学期可以开放多次
 */
class LaSession extends LongId with Named with InstantRange {
  var project: Project = _
  var semester: Semester = _
  var grades: String = _
}