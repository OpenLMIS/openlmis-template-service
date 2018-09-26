/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.template.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServerSecurityConfigurationTest {

  private static final String[] ALLOWED_ORIGINS = new String[]{"http://test.openlmis.org"};
  private static final String[] ALLOWED_METHODS = new String[]{"GET"};

  private ResourceServerSecurityConfiguration configuration =
      new ResourceServerSecurityConfiguration();

  @Test
  public void shouldCreateEmptyCorsConfigurationSourceBean() {
    ReflectionTestUtils.setField(configuration, "allowedOrigins", new String[0]);

    CorsConfigurationSource corsConfigurationSource = configuration.corsConfigurationSource();
    assertThat(corsConfigurationSource).isInstanceOf(UrlBasedCorsConfigurationSource.class);

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
        (UrlBasedCorsConfigurationSource) corsConfigurationSource;

    Map<String, CorsConfiguration> map = urlBasedCorsConfigurationSource
        .getCorsConfigurations();

    assertThat(map).isEmpty();
  }

  @Test
  public void shouldCreateCorsConfigurationSourceBean() {
    ReflectionTestUtils.setField(configuration, "allowedOrigins", ALLOWED_ORIGINS);
    ReflectionTestUtils.setField(configuration, "allowedMethods", ALLOWED_METHODS);

    CorsConfigurationSource corsConfigurationSource = configuration.corsConfigurationSource();
    assertThat(corsConfigurationSource).isInstanceOf(UrlBasedCorsConfigurationSource.class);

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
        (UrlBasedCorsConfigurationSource) corsConfigurationSource;

    Map<String, CorsConfiguration> map = urlBasedCorsConfigurationSource
        .getCorsConfigurations();

    assertThat(map).containsKey("/**");

    CorsConfiguration corsConfiguration = map.get("/**");

    assertThat(corsConfiguration.getAllowedOrigins()).contains(ALLOWED_ORIGINS);
    assertThat(corsConfiguration.getAllowedMethods()).contains(ALLOWED_METHODS);

  }
}
