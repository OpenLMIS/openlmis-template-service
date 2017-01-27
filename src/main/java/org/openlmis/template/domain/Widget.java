package org.openlmis.template.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "widget", schema = "template")
@NoArgsConstructor
public class Widget extends BaseEntity {
  private static final String TEXT = "text";

  @Column(nullable = false, columnDefinition = TEXT)
  @Getter
  @Setter
  private String name;


}