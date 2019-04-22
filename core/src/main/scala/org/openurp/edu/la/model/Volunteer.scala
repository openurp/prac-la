package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated
import org.openurp.edu.base.model.Student

/**
 * 志愿
 */
class Volunteer extends LongId with Updated {

  var std: Student = _
  
  var indexno: Int = _

  var option: LaOption = _

}