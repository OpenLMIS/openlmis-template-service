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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import guru.nidi.ramltester.junit.RamlMatchers;
import java.util.Collections;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.template.WidgetDataBuilder;
import org.openlmis.template.domain.Widget;
import org.openlmis.template.i18n.MessageKeys;
import org.openlmis.template.web.widget.WidgetDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SuppressWarnings("PMD.TooManyMethods")
public class WidgetControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/widgets";
  private static final String ID_URL = RESOURCE_URL + "/{id}";

  private Widget widget = new WidgetDataBuilder().build();
  private WidgetDto widgetDto = WidgetDto.newInstance(widget);

  @Before
  public void setUp() {
    given(widgetRepository.saveAndFlush(any(Widget.class))).willAnswer(new SaveAnswer<>());
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
        .body("name", is(widgetDto.getName()));

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
    given(widgetRepository.findOne(widgetDto.getId())).willReturn(widget);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body(ID, is(widgetDto.getId().toString()))
        .body("name", is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnNotFoundMessageIfWidgetDoesNotExistForGivenWidgetEndpoint() {
    given(widgetRepository.findOne(widgetDto.getId())).willReturn(null);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_NOT_FOUND));

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
    given(widgetRepository.findOne(widgetDto.getId())).willReturn(widget);

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
        .body("name", is(widgetDto.getName()));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnNotFoundMessageIfWidgetDoesNotExistForUpdateWidgetEndpoint() {
    given(widgetRepository.findOne(widgetDto.getId())).willReturn(null);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .pathParam(ID, widgetDto.getId().toString())
        .body(widgetDto)
        .when()
        .put(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_NOT_FOUND));

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
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_ID_MISMATCH));

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
    given(widgetRepository.exists(widgetDto.getId())).willReturn(true);

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
    given(widgetRepository.exists(widgetDto.getId())).willReturn(false);

    restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam(ID, widgetDto.getId().toString())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(HttpStatus.SC_NOT_FOUND)
        .body(MESSAGE_KEY, is(MessageKeys.ERROR_NOT_FOUND));

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

}
