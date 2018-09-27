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

package org.openlmis.template.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.Test;
import org.openlmis.template.WidgetDataBuilder;
import org.openlmis.template.domain.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

public class WidgetRepositoryIntegrationTest extends BaseCrudRepositoryIntegrationTest<Widget> {

  @Autowired
  private WidgetRepository widgetRepository;

  @Override
  CrudRepository<Widget, UUID> getRepository() {
    return widgetRepository;
  }

  @Override
  Widget generateInstance() {
    return new WidgetDataBuilder()
        .withName("name" + getNextInstanceNumber())
        .withName("code" + getNextInstanceNumber())
        .buildAsNew();
  }

  @Test
  public void shouldAllowForSeveralWidgetsWithoutCode() {
    long count = widgetRepository.count();

    Widget widget1 = new WidgetDataBuilder()
        .withName("name" + getNextInstanceNumber())
        .withCode(null)
        .buildAsNew();
    Widget widget2 = new WidgetDataBuilder()
        .withName("name" + getNextInstanceNumber())
        .withCode(null)
        .buildAsNew();

    widgetRepository.saveAndFlush(widget1);
    widgetRepository.saveAndFlush(widget2);

    assertThat(widgetRepository.count()).isEqualTo(count + 2);
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void shouldNotAllowForSeveralWidgetsWithSameCode() {
    Widget widget1 = generateInstance();
    Widget widget2 = generateInstance();
    widget2.setCode(widget1.getCode());

    widgetRepository.saveAndFlush(widget1);
    widgetRepository.saveAndFlush(widget2);
  }
}
