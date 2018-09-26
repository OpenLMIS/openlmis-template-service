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

package org.openlmis.template.i18n;

public abstract class MessageKeys {

  private static final String SERVICE_PREFIX = "template";
  private static final String ERROR_PREFIX = SERVICE_PREFIX + ".error";

  public static final String ERROR_NOT_FOUND = ERROR_PREFIX
      + ".widgetNotFound";

  public static final String ERROR_JAVERS_EXISTING_ENTRY = ERROR_PREFIX
      + ".javers.entryAlreadyExists";

  public static final String ERROR_ID_MISMATCH = ERROR_PREFIX + ".idMismatch";

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }
}
