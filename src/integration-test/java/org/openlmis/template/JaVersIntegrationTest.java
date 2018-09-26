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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.template.domain.Widget;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class JaVersIntegrationTest {

  @Resource(name = "javersProvider")
  private Javers javers;

  private static DateTimeZone defaultZone;
  private static final String COMMIT_AUTHOR = "author";

  @BeforeClass
  public static void beforeClass() {
    defaultZone = DateTimeZone.getDefault();
  }

  @After
  public void after() {
    DateTimeZone.setDefault(defaultZone);
  }

  @Test
  public void shouldAlwaysCommitWithUtcTimeZone() {

    //given
    Widget widget = new Widget();
    widget.setId(UUID.randomUUID());
    widget.setName("name_1");

    //when
    DateTimeZone.setDefault(DateTimeZone.forID("UTC"));
    javers.commit(COMMIT_AUTHOR, widget);

    DateTimeZone.setDefault(DateTimeZone.forID("Africa/Johannesburg"));
    widget.setName("name_2");
    javers.commit(COMMIT_AUTHOR, widget);

    //then
    List<CdoSnapshot> snapshots = javers.findSnapshots(
        QueryBuilder.byClass(Widget.class).build());
    assertEquals(2, snapshots.size());

    LocalDateTime commitTime1 = snapshots.get(0).getCommitMetadata().getCommitDate();
    LocalDateTime commitTime2 = snapshots.get(1).getCommitMetadata().getCommitDate();

    int delta = Math.abs(Seconds.secondsBetween(commitTime1, commitTime2).getSeconds());
    assertTrue(delta < 1);
  }
}
