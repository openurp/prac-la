package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated
import org.openurp.edu.base.model.Student
import org.beangle.commons.collection.Collections
import scala.collection.mutable.Buffer

/**
 * 申请
 */
class Registration extends LongId with Updated {

  var std: Student = _

  var volunteers: Buffer[Volunteer] = Collections.newBuffer[Volunteer]

}