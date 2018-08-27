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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.javers.common.date.DateProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/**
 * This class may be used by JaVers to retrieve the LocalDateTime that it associates with commits.
 * It is intended to be used, rather than JaVers' default DateProvider, so as to be explicit and
 * consistent with the use of UTC within JaVers' domain. (Otherwise, JaVers uses the default
 * system timezone, which may change, when constructing a LocalDateTime.)
 */
public class JaVersDateProvider implements DateProvider {
  public static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.UTC;
  public static final ZoneId ZONE_ID = ZoneId.of(DATE_TIME_ZONE.getID());

  public LocalDateTime now() {
    return LocalDateTime.now(DATE_TIME_ZONE);
  }

  /**
   * Converts the specified LocalDateTime to a ZonedDateTime in UTC.
   */
  public static ZonedDateTime getZonedDateTime(LocalDateTime localDateTime) {

    /* Get an instant representing localDateTime with the understanding that it was stored
       using JaVersDateProvider.DATE_TIME_ZONE */
    long epoch = localDateTime.toDateTime(DATE_TIME_ZONE).getMillis();
    Instant instant = Instant.ofEpochMilli(epoch);

    //Convert the instant to a ZonedDateTime in UTC.
    return ZonedDateTime.ofInstant(instant, ZONE_ID);
  }

  /**
   * Returns a ZonedDateTime (in UTC!) nearly a billion years prior to the unixEpoch.
   */
  public static ZonedDateTime getMinDateTime() {
    /* The earliest ZonedDateTime value is -999999999 years prior to unixEpoch.
       Instant.MIN, however, falls in the year -1000000000. Therefore, add just
       over a year to it. */
    Instant minInstant = Instant.MIN.plus(Duration.ofDays(366));

    //Because we're returning such an extreme value, its timezone is irrelevant.
    return ZonedDateTime.ofInstant(minInstant, ZONE_ID);
  }

  /**
   * Returns a ZonedDateTime (in UTC!) nearly a billion years past the unixEpoch.
   */
  public static ZonedDateTime getMaxDateTime() {
    Instant maxInstant = Instant.MAX.minus(Duration.ofDays(366));
    return ZonedDateTime.ofInstant(maxInstant, ZONE_ID);
  }
}
