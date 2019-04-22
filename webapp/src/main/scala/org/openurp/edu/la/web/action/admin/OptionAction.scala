package org.openurp.edu.la.web.action.admin

import java.time.LocalDate

import org.beangle.commons.collection.Order
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.base.model.Project
import org.openurp.edu.base.model.Semester
import org.openurp.edu.la.model.Corporation
import org.openurp.edu.la.model.LaOption

class OptionAction extends RestfulAction[LaOption] {

  override protected def indexSetting(): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    super.indexSetting()
  }

  override protected def editSetting(entity: LaOption): Unit = {
    put("semesters", entityDao.getAll(classOf[Semester]))
    put("currentSemester", getCurSemester())
    super.editSetting(entity)
  }

  def getCurSemester(): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
    builder.where("semester.beginOn <= :date and semester.endOn >= :date", LocalDate.now())
    val semesters = entityDao.search(builder)
    semesters(0)
  }

  override protected def getQueryBuilder(): OqlBuilder[LaOption] = {
    val status = get("status")
    val builder = OqlBuilder.from(classOf[LaOption], "option")
    status.foreach(
      a => a match {
        case "0" => {
          builder.where("option.actual<option.capacity")
        }
        case "1" => {
          builder.where("option.actual=option.capacity")
        }
        case "2" =>
      })
    populateConditions(builder)
    builder.orderBy(get(Order.OrderStr).orNull).limit(getPageLimit)
  }

  def corporation(): View = {
    val name = get("term").orNull
    val query = OqlBuilder.from(classOf[Corporation], "corporation")
    populateConditions(query)

    if (Strings.isNotEmpty(name)) {
      query.where("corporation.name like :name ", '%' + name + '%')
    }
    val pageLimit = getPageLimit
    query.limit(pageLimit);
    put("corporations", entityDao.search(query))
    put("pageLimit", pageLimit)
    forward()
  }

  override protected def saveAndRedirect(option: LaOption): View = {
    val projectId = get("project").get.toInt
    val project = entityDao.get(classOf[Project], projectId)
    option.project = project
    val corporationId = longId("laOption.corporation")
    val corporation = entityDao.get(classOf[Corporation], corporationId)
    if (get("option.request").isEmpty) {
      option.request = corporation.request
    }
    if (get("option.benefits").isEmpty) {
      option.benefits = corporation.benefits
    }
    option.actual = option.volunteers.size
    super.saveAndRedirect(option)
  }

}