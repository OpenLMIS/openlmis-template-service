/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.template;

import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.openlmis.template.domain.Widget;

public class WidgetDataBuilder {
  private UUID id = UUID.randomUUID();
  private String name = "name";
  private String code = RandomStringUtils.randomAlphanumeric(10);

  public WidgetDataBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public WidgetDataBuilder withCode(String code) {
    this.code = code;
    return this;
  }

  /**
   * Builds new instance of Widget (with id field).
   */
  public Widget build() {
    Widget widget = buildAsNew();
    widget.setId(id);

    return widget;
  }

  /**
   * Builds new instance of Widget as a new object (without id field).
   */
  public Widget buildAsNew() {
    Widget widget = new Widget();
    widget.setName(name);
    widget.setCode(code);

    return widget;
  }

}
