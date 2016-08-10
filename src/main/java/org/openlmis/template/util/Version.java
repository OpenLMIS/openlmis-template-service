package org.openlmis.template.util;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class containing version information.
 */
public class Version {

  public static final String VERSION = "version.properties";

  @Getter
  private String service = "service";

  @Getter
  private String build = "${build}";

  @Getter
  private String branch = "${branch}";

  @Getter
  private String timeStamp = "${time}";

  @Getter
  private String version = "version";

  private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

  /**
   * Class constructor used to fill Version with data from version file.
   */
  public Version() {

    InputStream inputStream;
    try {
      Properties properties = new Properties();
      inputStream = getClass().getClassLoader().getResourceAsStream(VERSION);
      if (inputStream != null) {
        properties.load(inputStream);
        service = properties.getProperty("Service");
        build = properties.getProperty("Build");
        branch = properties.getProperty("Branch");
        timeStamp = properties.getProperty("Timestamp");
        version = properties.getProperty("Version");
      }
    } catch (IOException ex) {
      LOGGER.error("Error loading version properties file");
    }
  }
}
