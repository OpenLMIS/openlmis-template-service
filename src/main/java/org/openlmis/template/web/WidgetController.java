package org.openlmis.template.web;

import org.openlmis.template.domain.Widget;
import org.openlmis.template.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller used to expose Widgets via HTTP.
 */
@RestController
@RequestMapping("/api")
public class WidgetController {

  private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);

  @Autowired
  private WidgetRepository widgetRepository;

  /**
   * Allows the creation of a new widget. If the id is specified, it will be ignored.
   */
  @RequestMapping(value = "/widgets", method = RequestMethod.POST)
  public ResponseEntity createFacility(@RequestBody Widget widget) {
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

  private ResponseEntity<?> handleNotFound()
  {
    //Note that most services throw a custom exception rather than
    // manually return a 404 as shown below.
    return new ResponseEntity(null, HttpStatus.BAD_REQUEST.NOT_FOUND);
  }

}
