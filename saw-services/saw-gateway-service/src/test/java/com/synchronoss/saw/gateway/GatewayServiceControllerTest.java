package com.synchronoss.saw.gateway;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.synchronoss.saw.gateway.controller.GatewayController;

@RunWith(SpringRunner.class)
@WebMvcTest(GatewayController.class)
@TestPropertySource(locations = "application-test.properties")
@WebAppConfiguration
public class GatewayServiceControllerTest {

  WireMockServer wireMockServer = null;

  @Before

  public void setup() throws Exception {
    wireMockServer = new WireMockServer(options().dynamicPort());
    wireMockServer.start();
  }

  @Autowired
  private MockMvc mockMvc;

  @Value("${security.service.host}")
  private String securityUrl;

  @Test
  public void gatewayControllerTestWithAuthorizationHeader() throws Exception {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("authorization", "localhost");
    MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.post(securityUrl + wireMockServer.port());
    builder.headers(requestHeaders).contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcResultHandlers.print());
  }


  @Test
  public void gatewayControllerTestWithoutAuthorizationHeader() throws Exception {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("host", "localhost");
    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/md")
        .headers(requestHeaders).contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andDo(MockMvcResultHandlers.print());
  }

  @org.junit.After
  public void tearDown() throws Exception {
    wireMockServer.stop();

  }
}
