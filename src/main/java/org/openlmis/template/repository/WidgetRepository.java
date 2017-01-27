package org.openlmis.template.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.openlmis.template.domain.Widget;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

@JaversSpringDataAuditable
public interface WidgetRepository extends PagingAndSortingRepository<Widget, UUID> {
}
