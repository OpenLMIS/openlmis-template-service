package org.openlmis.template;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockTest {

    private class WireMockTestClient {
        String resourceUrl = "http://localhost:8080/api/resource";

        String getResource() {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(resourceUrl, String.class);
        }

        void postMessage(String message) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String messageJson = "{\"message\": \"" + message + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(messageJson, headers);
            try {
                restTemplate.postForEntity(resourceUrl, entity, Object.class);
            } catch (HttpClientErrorException ignored) { }
        }
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void wireMockTest() {
        WireMockTestClient testClient = new WireMockTestClient();
        String responseJson = "{\"response\": \"Some content\"}";

        stubFor(get(urlEqualTo("/api/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)));

        String result = testClient.getResource();

        Assert.assertEquals(result, responseJson);

        testClient.postMessage("1234");

        verify(postRequestedFor(urlMatching("/api/resource"))
                .withRequestBody(matching(".*1234.*"))
                .withHeader("Content-Type", matching("application/json")));
    }

}
