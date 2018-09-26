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

package org.openlmis.template.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.template.util.Message.LocalizedMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

@RunWith(MockitoJUnitRunner.class)
public class MessageTest {
  private static final Locale LOCALE = Locale.ENGLISH;
  private static final String MESSAGE_KEY = "foo";

  @Mock
  private MessageSource messageSource;

  @Test(expected = NullPointerException.class)
  public void messageShouldRequireNonNullKey() {
    new Message(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void messageShouldRequireNonEmptyKey() {
    new Message(" ");
  }

  @Test(expected = NoSuchMessageException.class)
  public void humanStringShouldThrowExceptionIfKeyNotFound() {
    String key = "foo.bar";
    String p1 = "some";
    String p2 = "stuff";
    Message msg = new Message(key, p1, p2);

    when(messageSource.getMessage(key, new Object[]{p1, p2}, LOCALE))
        .thenThrow(NoSuchMessageException.class);
    msg.localMessage(messageSource, LOCALE);
  }

  @Test
  public void shouldCreateLocalizedMessageForValidKey() {
    String key = "foo.bar";
    String value = "foo some bar stuff";
    String p1 = "some";
    String p2 = "stuff";
    Message msg = new Message(key, p1, p2);

    when(messageSource.getMessage(key, new Object[]{p1, p2}, LOCALE)).thenReturn(value);
    LocalizedMessage localizedMessage = msg.localMessage(messageSource, LOCALE);

    assertThat(localizedMessage.asMessage()).isEqualTo(value);
    assertThat(localizedMessage.toString()).isEqualTo(key + ": " + value);
  }

  @Test
  public void toStringShouldHandleObjects() {
    String key = "key.something";
    Date today = new Date();
    Message message = new Message(key, "a", today);

    // expected is:  "key.something: a, <date>"
    assertEquals(key + ": " + "a" + ", " + today.toString(), message.toString());
  }

  @Test
  public void equalsShouldReturnTrueForSameObject() {
    Message message = new Message(MESSAGE_KEY);

    assertThat(message.equals(message)).isTrue();
  }

  @Test
  public void equalsShouldReturnFalseForNonMessageObject() {
    Message message = new Message(MESSAGE_KEY);
    Object obj = "abc";

    assertThat(message.equals(obj)).isFalse();
  }

  @Test
  public void equalsAndHashCodeShouldUseKey() {
    Message message1 = new Message(MESSAGE_KEY);
    Message message2 = new Message(MESSAGE_KEY);

    assertThat(message1.equals(message2)).isTrue();
    assertThat(message2.equals(message1)).isTrue();
    assertThat(message1.hashCode()).isEqualTo(message2.hashCode());
  }

  @Test
  public void equalsAndHashCodeShouldIgnoreSpace() {
    Message message1 = new Message(MESSAGE_KEY);
    Message message2 = new Message(" foo ");

    assertThat(message1.equals(message2)).isTrue();
    assertThat(message2.equals(message1)).isTrue();
    assertThat(message1.hashCode()).isEqualTo(message2.hashCode());
  }
}
