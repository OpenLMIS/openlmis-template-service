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
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis;

import static org.openlmis.utils.Pagination.DEFAULT_PAGE_NUMBER;
import static org.openlmis.utils.Pagination.DEFAULT_PAGE_SIZE;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.openlmis.requisition.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AuditLogInitializer runs after its associated Spring application has loaded.
 * It examines each domain object in the database and registers them with JaVers
 * if they haven't already been so. This is, in part, a fix for
 * <a href="https://github.com/javers/javers/issues/214">this issue</a>.
 */

@Component
@Profile("!test")
public class AuditLogInitializer implements CommandLineRunner {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private Javers javers;

  /**
   * This method is part of CommandLineRunner and is called automatically by Spring.
   * @param args Main method arguments.
   */
  public void run(String... args) {
    //Get all JaVers repositories.
    Map<String,Object> repositoryMap =
            applicationContext.getBeansWithAnnotation(JaversSpringDataAuditable.class);

    //For each one...
    for (Object object : repositoryMap.values()) {
      if (object instanceof PagingAndSortingRepository) {
        createSnapshots((PagingAndSortingRepository<?, ?>) object);
      } else if (object instanceof CrudRepository) {
        createSnapshots((CrudRepository<?, ?>) object);
      }
    }
  }

  private void createSnapshots(PagingAndSortingRepository<?, ?> repository) {
    Pageable pageable = new PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

    while (true) {
      Page<?> page = repository.findAll(pageable);

      if (!page.hasContent()) {
        break;
      }

      page.forEach(this::createSnapshot);

      pageable = pageable.next();
    }
  }

  private void createSnapshots(CrudRepository<?, ?> repository) {
    //... retrieve all of its domain objects and...
    repository.findAll().forEach(this::createSnapshot);
  }

  private void createSnapshot(Object object) {
    //...check whether there exists a snapshot for it in the audit log.
    // Note that we don't care about checking for logged changes, per se,
    // and thus use findSnapshots() rather than findChanges()
    BaseEntity baseEntity = (BaseEntity) object;

    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(baseEntity.getId(), object.getClass());
    List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

    //If there are no snapshots of the domain object, then take one
    if (snapshots.size() == 0) {
      javers.commit("System: AuditLogInitializer", baseEntity);
    }
  }

}
