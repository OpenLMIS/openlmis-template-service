package org.openlmis.template.web;

import org.openlmis.template.util.ServiceSignature;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ServiceNameController {

    @RequestMapping("/")
    public ServiceSignature index() {
        return new ServiceSignature(ServiceSignature.SERVICE_NAME, ServiceSignature.SERVICE_VERSION);
    }
}