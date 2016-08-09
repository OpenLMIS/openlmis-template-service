package org.openlmis.template.util;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

  Logger logger = LoggerFactory.getLogger(Version.class);

  /**
   * Allow displaying build information.
   */
  public Version() {

    InputStream inputStream = getClass().getResourceAsStream(VERSION);
    if (inputStream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      try {
        this.service = getValueFromLine(reader.readLine());
        this.build = getValueFromLine(reader.readLine());
        this.branch = getValueFromLine(reader.readLine());
        this.timeStamp = getValueFromLine(reader.readLine());
        this.version = getValueFromLine(reader.readLine());
        reader.close();
      } catch (IOException ex) {
        logger.error("Error reading line from file");
      }
    }
  }

  private String getValueFromLine(String line) {
    return line.substring(line.indexOf(' ') + 1);
  }
}
