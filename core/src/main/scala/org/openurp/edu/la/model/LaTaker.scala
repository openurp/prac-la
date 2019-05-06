package org.openurp.edu.la.model

import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.Updated

class LaTaker extends LongId with Updated {

  var volunteer: Volunteer = _

  var rank: Int = _

  var option: LaOption = _

}