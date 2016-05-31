package org.openlmis.template.i18n;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private ExposedMessageSource messageSource;

    @Setter
    @Getter
    private Locale currentLocale;

    @Autowired
    public MessageService(ExposedMessageSource messageSource) {
        this.messageSource = messageSource;
        this.currentLocale = Locale.ENGLISH;
    }

    public Map<String, String> getAllMessages() {
        return messageSource.getAllMessages(currentLocale);
    }
}
