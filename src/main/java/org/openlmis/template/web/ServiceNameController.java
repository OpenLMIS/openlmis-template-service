package org.openlmis.template.web;

import org.openlmis.template.util.ServiceSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ServiceNameController {

    Logger logger = LoggerFactory.getLogger(ServiceNameController.class);

    @Autowired
    private MessageSource messageSource;
    
    @RequestMapping("/")
    public ServiceSignature index() {
        logger.info("Returning service name and version");
        return new ServiceSignature(ServiceSignature.SERVICE_NAME, ServiceSignature.SERVICE_VERSION);
    }

    @RequestMapping("/hello")
    public String hello() {
        logger.info("Returning hello world message");
        return messageSource.getMessage("msg.hello.world", null, LocaleContextHolder.getLocale());
    }
}