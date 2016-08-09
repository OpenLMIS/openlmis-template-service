package org.openlmis.template.util;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class containing version information.
 */
public class Version {

  public static final String VERSION = "/version";

  @Getter
  @Setter
  private String service = "service";

  @Getter
  @Setter
  private String build = "${build}";

  @Getter
  @Setter
  private String branch = "${branch}";

  @Getter
  @Setter
  private String timeStamp = "${time}";

  @Getter
  @Setter
  private String version = "version";

  private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

  /**
   * Class constructor used to fill Version with data from version file.
   */
  public Version() {

    InputStream inputStream = getClass().getResourceAsStream(VERSION);
    if (inputStream != null) {
      try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        this.service = getValueFromLine(reader.readLine());
        this.build = getValueFromLine(reader.readLine());
        this.branch = getValueFromLine(reader.readLine());
        this.timeStamp = getValueFromLine(reader.readLine());
        this.version = getValueFromLine(reader.readLine());
      } catch (IOException ex) {
        LOGGER.error("Error reading version information from version file");
      }
    }
  }

  private String getValueFromLine(String line) {
    return line.substring(line.indexOf(' ') + 1);
  }
}
