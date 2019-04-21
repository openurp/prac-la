package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Named
import org.beangle.data.model.pojo.Remark
import org.beangle.data.model.pojo.Updated

/**
 * 项目单位
 */
class LaDepart extends LongId with Named with Updated with Remark {
  
  var opened: Boolean = false
  var request: Option[String] = None
  var benefits: Option[String] = None
  var capacity: Int = _
  
}