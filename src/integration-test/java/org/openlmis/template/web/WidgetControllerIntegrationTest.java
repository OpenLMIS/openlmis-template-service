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

package org.openlmis.template.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import guru.nidi.ramltester.junit.RamlMatchers;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.repository.jql.JqlQuery;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.template.WidgetDataBuilder;
import org.openlmis.template.domain.Widget;
import org.openlmis.template.i18n.MessageKeys;
import org.openlmis.template.web.widget.WidgetController;
import org.openlmis.template.web.widget.WidgetDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SuppressWarnings("PMD.TooManyMethods")
public class WidgetControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = WidgetController.RESOURCE_PATH;
  private static final String ID_URL = RESOURCE_URL + "/{id}";
  private static final String AUDIT_LOG_URL = ID_URL + "/auditLog";

  private static final String NAME = "name";

  private Widget widget = new WidgetDataBuilder().build();
  private WidgetDto widgetDto = WidgetDto.newInstance(widget);

  private GlobalId globalId = new UnboundedValueObjectId(Widget.class.getSimpleName());
  private ValueChange change = new ValueChange(globalId, NAME, "name1", "name2");

  private CommitId commitId = new CommitId(1, 0);
  private CommitMetadata commitMetadata = new CommitMetadata(
      "admin", Maps.newHashMap(), LocalDateTime.now(), commitId);

  @Before
  public void setUp() {
    given(widgetRepository.saveAndFlush(any(Widget.class))).willAnswer(new SaveAnswer<>());
    change.bindToCommit(commitMetadata);
  }

  @Test
  public void shouldReturnPageOfWidgets() {
    given(widgetRepository.findAll(any(Pageable.class)))
        .willReturn(new PageImpl<>(Collections.singletonList(widget)));

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .queryParam("page", pageable.getPageNumber())
        .queryParam("size", pageable.getPageSize())
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("content", hasSize(1))
        .body("content[0].id", is(widget.getId().toString()))
        .body("content[0].name", is(widget.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForAllWidgetsEndpointIfUserIsNotAuthorized() {
    restAssured.given()
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldCreateWidget() {
    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(widgetDto)
        .when()
        .post(RESOURCE_URL)
        .then()
        .statusCode(HttpStatus.SC_CREATED)
        .body(ID, is(notNullValue()))
        .body(NAME, is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForCreateWidgetEndpointIfUserIsNotAuthorized() {
    restAssured
        .given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(widgetDto)
        .when()
        .post(RESOURCE_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnGivenWidget() {
    given(widgetRepository.findById(widgetDto.getId())).willReturn(Optional.of(widget));

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body(ID, is(widgetDto.getId().toString()))
        .body(NAME, is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnNotFoundMessageIfWidgetDoesNotExistForGivenWidgetEndpoint() {
    given(widgetRepository.findById(widgetDto.getId())).willReturn(Optional.empty());

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_WIDGET_NOT_FOUND));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForGetWidgetEndpointIfUserIsNotAuthorized() {
    restAssured
        .given()
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateWidget() {
    given(widgetRepository.findById(widgetDto.getId())).willReturn(Optional.of(widget));

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .pathParam(ID, widgetDto.getId().toString())
        .body(widgetDto)
        .when()
        .put(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body(ID, is(widgetDto.getId().toString()))
        .body(NAME, is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldCreateWidgetIfWidgetDoesNotExistForUpdateWidgetEndpoint()
      throws JsonProcessingException {
    String widgetJson = "{\"code\":\"" + widgetDto.getCode() + "\",\"name\":\""
        + widgetDto.getName() + "\"}";
    Map<String, String> widgetMap = new ObjectMapper().readValue(widgetJson,
        new TypeReference<Map<String, String>>() {});
    UUID pathId = UUID.randomUUID();
    given(widgetRepository.findById(pathId)).willReturn(Optional.empty());

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .pathParam(ID, pathId)
        .body(widgetMap)
        .when()
        .put(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body(ID, is(pathId.toString()))
        .body(NAME, is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnBadRequestMessageIfWidgetCannotBeUpdated() {
    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .pathParam(ID, UUID.randomUUID().toString())
        .body(widgetDto)
        .when()
        .put(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_WIDGET_ID_MISMATCH));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForUpdateWidgetEndpointIfUserIsNotAuthorized() {
    restAssured
        .given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .pathParam(ID, widgetDto.getId().toString())
        .body(widgetDto)
        .when()
        .put(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldDeleteWidget() {
    given(widgetRepository.existsById(widgetDto.getId())).willReturn(true);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnNotFoundMessageIfWidgetDoesNotExistForDeleteWidgetEndpoint() {
    given(widgetRepository.existsById(widgetDto.getId())).willReturn(false);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_WIDGET_NOT_FOUND));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForDeleteWidgetEndpointIfUserIsNotAuthorized() {
    restAssured
        .given()
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAuditLogs() {
    given(widgetRepository.existsById(widgetDto.getId())).willReturn(true);
    willReturn(Lists.newArrayList(change)).given(javers).findChanges(any(JqlQuery.class));

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(AUDIT_LOG_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("", hasSize(1))
        .body("changeType", hasItem(change.getClass().getSimpleName()))
        .body("globalId.valueObject", hasItem(Widget.class.getSimpleName()))
        .body("commitMetadata.author", hasItem(commitMetadata.getAuthor()))
        .body("commitMetadata.properties", hasItem(hasSize(0)))
        .body("commitMetadata.commitDate", hasItem(commitMetadata.getCommitDate().toString()))
        .body("commitMetadata.id", hasItem(commitId.valueAsNumber().floatValue()))
        .body("property", hasItem(change.getPropertyName()))
        .body("left", hasItem(change.getLeft().toString()))
        .body("right", hasItem(change.getRight().toString()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAuditLogsWithParameters() {
    given(widgetRepository.existsById(widgetDto.getId())).willReturn(true);
    willReturn(Lists.newArrayList(change)).given(javers).findChanges(any(JqlQuery.class));

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .queryParam("author", commitMetadata.getAuthor())
        .queryParam("changedPropertyName", change.getPropertyName())
        .when()
        .get(AUDIT_LOG_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("", hasSize(1))
        .body("changeType", hasItem(change.getClass().getSimpleName()))
        .body("globalId.valueObject", hasItem(Widget.class.getSimpleName()))
        .body("commitMetadata.author", hasItem(commitMetadata.getAuthor()))
        .body("commitMetadata.properties", hasItem(hasSize(0)))
        .body("commitMetadata.commitDate", hasItem(commitMetadata.getCommitDate().toString()))
        .body("commitMetadata.id", hasItem(commitId.valueAsNumber().floatValue()))
        .body("property", hasItem(change.getPropertyName()))
        .body("left", hasItem(change.getLeft().toString()))
        .body("right", hasItem(change.getRight().toString()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnNotFoundMessageIfWidgetDoesNotExistForAuditLogEndpoint() {
    given(widgetRepository.existsById(widgetDto.getId())).willReturn(false);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(AUDIT_LOG_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedForAuditLogEndpointIfUserIsNotAuthorized() {
    restAssured
        .given()
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(AUDIT_LOG_URL)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
}
