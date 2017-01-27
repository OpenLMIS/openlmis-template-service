package org.openlmis.template.util;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.UUIDGenerator;
import org.openlmis.template.domain.BaseEntity;

import java.io.Serializable;

public class ConditionalUuidGenerator extends UUIDGenerator {

  @Override
  public Serializable generate(SessionImplementor session, Object object) {
    if ((((BaseEntity) object).getId()) == null) {
      return super.generate(session, object);
    } else {
      return ((BaseEntity) object).getId();
    }
  }
}
