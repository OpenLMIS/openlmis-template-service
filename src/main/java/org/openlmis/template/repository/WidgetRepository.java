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

import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.openlmis.template.domain.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

@JaversSpringDataAuditable
public interface WidgetRepository extends PagingAndSortingRepository<Widget, UUID>,
      BaseAuditableRepository<Widget,  UUID> {

  @Query(value = "SELECT\n"
      + "    w.*\n"
      + "FROM\n"
      + "    template.widget w\n"
      + "WHERE\n"
      + "    id NOT IN (\n"
      + "        SELECT\n"
      + "            id\n"
      + "        FROM\n"
      + "            template.widget w\n"
      + "            INNER JOIN template.jv_global_id g "
      + "ON CAST(w.id AS varchar) = SUBSTRING(g.local_id, 2, 36)\n"
      + "            INNER JOIN template.jv_snapshot s  ON g.global_id_pk = s.global_id_fk\n"
      + "    )\n",
      nativeQuery = true)
  Page<Widget> findAllWithoutSnapshots(Pageable pageable);
}
