package org.openlmis.template.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.TypeName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@TypeName("Widget")
@Table(name = "widget", schema = "template")
@NoArgsConstructor
public class Widget extends BaseEntity {
  private static final String TEXT = "text";

  @Column(nullable = false, columnDefinition = TEXT)
  @Getter
  @Setter
  private String name;


}