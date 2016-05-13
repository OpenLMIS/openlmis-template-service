package org.openlmis.template.web;

import org.openlmis.template.util.ServiceSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ServiceNameController {

    @Autowired
    private MessageSource messageSource;
    
    @RequestMapping("/")
    public ServiceSignature index() {
        return new ServiceSignature(ServiceSignature.SERVICE_NAME, ServiceSignature.SERVICE_VERSION);
    }

    @RequestMapping("/hello")
    public String hello() {
        return messageSource.getMessage("msg.hello.world", null, LocaleContextHolder.getLocale());
    }
}