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
package org.openurp.prac.la.web

import org.beangle.cdi.bind.BindModule
import org.beangle.ems.app.datasource.AppDataSourceFactory
import org.openurp.prac.la.web.action.student.EnrollAction
import org.openurp.prac.la.web.action.admin.{CorporationAction, OptionAction, SessionAction, TakerAction, VolunteerAction}

class DefaultModule extends BindModule {

  protected override def binding() {
    bind(classOf[CorporationAction],classOf[OptionAction],classOf[SessionAction],classOf[TakerAction])
    bind(classOf[EnrollAction],classOf[VolunteerAction])

    bind("eamsDataSource",classOf[AppDataSourceFactory]).property("name", "eams")
  }
}
