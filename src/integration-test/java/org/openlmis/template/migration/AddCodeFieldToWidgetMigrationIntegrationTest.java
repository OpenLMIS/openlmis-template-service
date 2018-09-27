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

package org.openlmis.template.migration;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for orderable's data migrations.
 */
public class AddCodeFieldToWidgetMigrationIntegrationTest extends BaseMigrationIntegrationTest {

  @Override
  void insertDataBeforeMigration() {
    save(TABLE_WIDGET, generateWidget("4d1115de-0f60-408a-8a1e-44401e20a5b0", "name1"));
    save(TABLE_WIDGET, generateWidget("311a177f-5a06-4c5e-9417-9b3951687ba4", "name2"));
    save(TABLE_WIDGET, generateWidget("65537629-5852-464a-a845-f5e9b23e84f1", "name3"));
  }

  @Override
  String getTargetBeforeTestMigration() {
    return null;
  }

  @Override
  String getTestMigrationTarget() {
    return "20180927081245782";
  }

  @Override
  void verifyDataAfterMigration() {
    List<Map<String, Object>> rows = getRows(TABLE_WIDGET);
    for (Map<String, Object> row : rows) {
      assertThat(row)
          .containsKey("id")
          .containsKey("name")
          .containsKey("code");

      assertThat(row.get("id")).isNotNull();
      assertThat(row.get("name")).isIn("name1", "name2", "name3");
      assertThat(row.get("code")).isNull();
    }
  }

  private Map<String, Object> generateWidget(String id, String name) {
    return ImmutableMap
        .<String, Object>builder()
        .put("id", id)
        .put("name", name)
        .build();
  }
}
