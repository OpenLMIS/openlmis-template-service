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

import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Extension of {@link PagingAndSortingRepository} to enable using generic parameters
 * in creating Javers logs and provide additional methods to retrieve entities
 * using the pagination and sorting abstraction to ensure.
 */
@NoRepositoryBean
public interface BaseAuditableRepository<T, I extends Serializable>
    extends PagingAndSortingRepository<T, I> {

  /**
   * Returns a {@link Page} of entities which there are no Javers logs created for.
   */
  Page<T> findAllWithoutSnapshots(Pageable pageable);
}
