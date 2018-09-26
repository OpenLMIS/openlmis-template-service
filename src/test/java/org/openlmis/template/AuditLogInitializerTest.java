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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.template.domain.Widget;
import org.openlmis.template.repository.WidgetRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CdoSnapshot.class})
public class AuditLogInitializerTest {

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private Javers javers;

  @Mock
  private WidgetRepository tradeItemRepository;

  @Mock
  private CdoSnapshot snapshot;

  @Mock
  private TestItemRepository testItemRepository;

  private AuditLogInitializer auditLogInitializer;

  private Map<String, Object> repositoryMap = new HashMap<>();

  @Before
  public void setUp() {
    auditLogInitializer = new AuditLogInitializer(applicationContext, javers);

    when(applicationContext.getBeansWithAnnotation(JaversSpringDataAuditable.class))
        .thenReturn(repositoryMap);
  }

  @Test
  public void shouldNotCreateSnapshotIfRepositoryDoesNotExtendsRequiredInterface() {
    repositoryMap.put("testItemRepository", testItemRepository);

    auditLogInitializer.run();

    verifyZeroInteractions(javers);
  }

  @Test
  public void shouldNotCreateSecondSnapshot() {
    repositoryMap.put("tradeItemRepository", tradeItemRepository);

    ArrayList<Widget> content = Lists.newArrayList(new WidgetDataBuilder().build());
    Page<Widget> page = new PageImpl<>(content);

    when(tradeItemRepository.findAllWithoutSnapshots(any(Pageable.class)))
        .thenReturn(page)
        .thenReturn(new PageImpl<>(Lists.newArrayList()));

    when(javers.findSnapshots(any(JqlQuery.class)))
        .thenReturn(Lists.newArrayList(snapshot));

    auditLogInitializer.run();
    verify(javers, never()).commit(eq("System: AuditLogInitializer"), any());
  }

  @Getter
  @AllArgsConstructor
  class TestItem {

    UUID id;
  }

  @JaversSpringDataAuditable
  interface TestItemRepository
      extends PagingAndSortingRepository<TestItem, UUID> {

  }
}
