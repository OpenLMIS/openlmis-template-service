package org.openlmis.template.repository;

import org.openlmis.template.domain.Widget;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface WidgetRepository extends PagingAndSortingRepository<Widget, UUID> {
}
