package org.openlmis.template.web;

import org.openlmis.template.i18n.ExposedMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageController {

    Logger logger = LoggerFactory.getLogger(ServiceNameController.class);

    @Autowired
    private ExposedMessageSource messageSource;

    @RequestMapping("/messages")
    public Map<String, String> getAllMessages() {
        logger.info("Returning all messages for current locale");
        return messageSource.getAllMessages(LocaleContextHolder.getLocale());
    }
}
