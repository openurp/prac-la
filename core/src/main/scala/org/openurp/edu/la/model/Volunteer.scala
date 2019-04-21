package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated

/**
 * 志愿
 */
class Volunteer extends LongId with Updated {

  var indexno: Int = _

  var laDepart: LaDepart = _

}