package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Named

class Corporation extends LongId with Named {
  var request: Option[String] = None
  var benefits: Option[String] = None
}