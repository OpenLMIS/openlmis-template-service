package org.openlmis.template.web;

import org.openlmis.template.domain.Widget;
import org.openlmis.template.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller used to expose Widgets via HTTP.
 */
@RestController
public class WidgetController extends BaseController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);

  @Autowired
  private WidgetRepository widgetRepository;

  /**
   * Allows the creation of a new widget. If the id is specified, it will be ignored.
   */
  @RequestMapping(value = "/widgets", method = RequestMethod.POST)
  public ResponseEntity createWidget(@RequestBody Widget widget) {
    LOGGER.debug("Creating new widget");
    Widget newWidget = widgetRepository.save(widget);
    return new ResponseEntity(newWidget, HttpStatus.CREATED);
  }

  /**
   * Updates the specified widget.
   */
  @RequestMapping(value = "/widgets/{id}", method = RequestMethod.PUT)
  public ResponseEntity saveWidget(
          @PathVariable("id") UUID id, @RequestBody Widget widget) {
    LOGGER.debug("Updating widget");
    widget.setId(id);
    widgetRepository.save(widget);
    return new ResponseEntity(widget, HttpStatus.OK);
  }

  /**
   * Deletes the specified widget.
   */
  @RequestMapping(value = "/widgets/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteWidget(@PathVariable("id") UUID id) {

    Widget widget = widgetRepository.findOne(id);
    if (widget == null) {
      return handleNotFound();
    } else {
      widgetRepository.delete(widget);
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
  }

  /**
   * Retrieves all widgets. Note that an empty collection rather than a 404 should be
   * returned if no widgets exist.
   */
  @RequestMapping(value = "/widgets", method = RequestMethod.GET)
  public ResponseEntity getAllWidgets() {
    Iterable<Widget> widgets = widgetRepository.findAll();
    return new ResponseEntity(widgets, HttpStatus.OK);
  }

  /**
   * Retrieves the specified widget.
   */
  @RequestMapping(value = "/widgets/{id}", method = RequestMethod.GET)
  public ResponseEntity getSpecifiedWidget(@PathVariable("id") UUID id) {
    Widget widget = widgetRepository.findOne(id);
    if (widget == null) {
      return handleNotFound();
    } else {
      return new ResponseEntity(widget, HttpStatus.OK);
    }
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
  @RequestMapping(value = "/widgets/{id}/auditLog", method = RequestMethod.GET)
  public ResponseEntity<?> getWidgetAuditLog(
          @PathVariable("id") UUID id,
          @RequestParam(name = "author", required = false, defaultValue = "") String author,
          @RequestParam(name = "changedPropertyName", required = false, defaultValue = "")
                  String changedPropertyName,
          //Because JSON is all we formally support, returnJSON is excluded from our JavaDoc
          @RequestParam(name = "returnJSON", required = false, defaultValue = "true")
                  boolean returnJson,
          Pageable page) {

    //Return a 404 if the specified widget can't be found
    Widget widget = widgetRepository.findOne(id);
    if (widget == null) {
      return handleNotFound();
    }

    String auditData = getAuditLog(Widget.class, id, author, changedPropertyName, page, returnJson);
    return ResponseEntity.status(HttpStatus.OK).body(auditData);
  }

  private ResponseEntity<?> handleNotFound() {
    //Note that most services throw a custom exception rather than
    // manually return a 404 as shown below.
    return new ResponseEntity(null, HttpStatus.BAD_REQUEST.NOT_FOUND);
  }

}
