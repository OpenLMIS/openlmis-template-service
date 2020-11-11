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

package org.openlmis.template.web.widget;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.template.domain.Widget;
import org.openlmis.template.exception.NotFoundException;
import org.openlmis.template.exception.ValidationMessageException;
import org.openlmis.template.i18n.MessageKeys;
import org.openlmis.template.repository.WidgetRepository;
import org.openlmis.template.util.Pagination;
import org.openlmis.template.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller used to expose Widgets via HTTP.
 */
@Controller
@RequestMapping(WidgetController.RESOURCE_PATH)
@Transactional
public class WidgetController extends BaseController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);

  public static final String RESOURCE_PATH = API_PATH + "/widgets";

  @Autowired
  private WidgetRepository widgetRepository;

  /**
   * Allows the creation of a new widget. If the id is specified, it will be ignored.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public WidgetDto createWidget(@RequestBody WidgetDto widget) {
    LOGGER.debug("Creating new widget");
    Widget newWidget = Widget.newInstance(widget);
    newWidget.setId(null);
    newWidget = widgetRepository.saveAndFlush(newWidget);

    return WidgetDto.newInstance(newWidget);
  }

  /**
   * Updates the specified widget.
   */
  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public WidgetDto saveWidget(@PathVariable("id") UUID id, @RequestBody WidgetDto widget) {
    if (null != widget.getId() && !Objects.equals(widget.getId(), id)) {
      throw new ValidationMessageException(MessageKeys.ERROR_WIDGET_ID_MISMATCH);
    }

    LOGGER.debug("Updating widget");
    Widget db;
    Optional<Widget> widgetOptional = widgetRepository.findById(id);
    if (widgetOptional.isPresent()) {
      db = widgetOptional.get();
      db.updateFrom(widget);
    } else {
      db = Widget.newInstance(widget);
      db.setId(id);
    }

    widgetRepository.saveAndFlush(db);

    return WidgetDto.newInstance(db);
  }

  /**
   * Deletes the specified widget.
   */
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteWidget(@PathVariable("id") UUID id) {
    if (!widgetRepository.existsById(id)) {
      throw new NotFoundException(MessageKeys.ERROR_WIDGET_NOT_FOUND);
    }

    widgetRepository.deleteById(id);
  }

  /**
   * Retrieves all widgets. Note that an empty collection rather than a 404 should be
   * returned if no widgets exist.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<WidgetDto> getAllWidgets(Pageable pageable) {
    Page<Widget> page = widgetRepository.findAll(pageable);
    List<WidgetDto> content = page
        .getContent()
        .stream()
        .map(WidgetDto::newInstance)
        .collect(Collectors.toList());
    return Pagination.getPage(content, pageable, page.getTotalElements());
  }

  /**
   * Retrieves the specified widget.
   */
  @GetMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public WidgetDto getSpecifiedWidget(@PathVariable("id") UUID id) {
    Widget widget = widgetRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(MessageKeys.ERROR_WIDGET_NOT_FOUND));

    return WidgetDto.newInstance(widget);
  }

  /**
   * Retrieves audit information related to the specified widget.
   *
   * @param author The author of the changes which should be returned.
   *               If null or empty, changes are returned regardless of author.
   * @param changedPropertyName The name of the property about which changes should be returned.
   *               If null or empty, changes associated with any and all properties are returned.
   * @param page A Pageable object that allows client to optionally add "page" (page number)
   *             and "size" (page size) query parameters to the request.
   */
  @GetMapping(value = "/{id}/auditLog")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<String> getWidgetAuditLog(@PathVariable("id") UUID id,
      @RequestParam(name = "author", required = false, defaultValue = "") String author,
      @RequestParam(name = "changedPropertyName", required = false, defaultValue = "")
          String changedPropertyName, Pageable page) {

    //Return a 404 if the specified instance can't be found
    if (!widgetRepository.existsById(id)) {
      throw new NotFoundException(MessageKeys.ERROR_WIDGET_NOT_FOUND);
    }

    return getAuditLogResponse(Widget.class, id, author, changedPropertyName, page);
  }

}
