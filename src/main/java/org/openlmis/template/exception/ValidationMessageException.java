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

package org.openlmis.template.exception;

import org.openlmis.template.util.Message;

/**
 * Exception for indicating that some input or constraint is invalid.  This should result in a
 * BAD REQUEST api response.
 */
public class ValidationMessageException extends BaseMessageException {

  /**
   * Create new validation exception with the given message key.  Helper method that
   * uses {@link #ValidationMessageException(Message)}.
   * @param messageKey the messageKey of a {@link Message}.
   */
  public ValidationMessageException(String messageKey) {
    super(messageKey);
  }

  /**
   * Create a new validation exception with the given message.
   * @param message the message.
   */
  public ValidationMessageException(Message message) {
    super(message);
  }

  /**
   * Create a new validation exception with the given message and cause.
   * @param message the message.
   * @param cause   the exception.
   */
  public ValidationMessageException(Throwable cause, Message message) {
    super(message, cause);
  }

  /**
   * Create a new validation exception with the given message and cause.
   * Message is constructed from message key and message parameters.
   * @param cause             the exception.
   * @param messageKey        the message key for new Message.
   * @param messageParameters the parameters for Message.
   */
  public ValidationMessageException(
      Throwable cause, String messageKey, Object... messageParameters) {
    super(new Message(messageKey, messageParameters), cause);
  }
}
