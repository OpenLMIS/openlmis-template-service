package org.openlmis.template.i18n;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Component
public class ExposedMessageSourceImpl extends ReloadableResourceBundleMessageSource implements ExposedMessageSource {

    protected Properties getAllProperties(Locale locale) {
        clearCacheIncludingAncestors();
        ReloadableResourceBundleMessageSource.PropertiesHolder propertiesHolder = getMergedProperties(locale);
        return propertiesHolder.getProperties();
    }

    public Map<String, String> getAllMessages(Locale locale) {
        Properties p = getAllProperties(locale);
        Enumeration<String> keys = (Enumeration<String>) p.propertyNames();
        Map<String, String> asMap = new HashMap<>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            asMap.put(key, p.getProperty(key));
        }
        return asMap;
    }
}