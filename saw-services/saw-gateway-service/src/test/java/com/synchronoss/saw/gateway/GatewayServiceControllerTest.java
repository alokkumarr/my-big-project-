package com.synchronoss.saw.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.synchronoss.saw.gateway.controller.GatewayController;

@RunWith(SpringRunner.class)
@WebMvcTest(GatewayController.class)
@TestPropertySource(properties = {
		"security.service.host=http://localhost:9081" }, locations = "application-test.properties")
public class GatewayServiceControllerTest {

	@Rule
	public WireMockRule wireMockRule_1 = new WireMockRule(options().port(9081));
	WireMockServer wireMockServer = null;

	@Before

	public void setup() throws Exception {
		wireMockServer = new WireMockServer(options().port(9091));
		wireMockServer.start();
	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void gatewayControllerTestWithAuthorizationHeader() throws Exception {
		stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
				.withRequestBody(WireMock.equalTo(""))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		requestHeaders.set("authorization", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/md").headers(requestHeaders)
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andDo(MockMvcResultHandlers.print());
	}
	
	
	@Test
	public void gatewayControllerTestWithoutAuthorizationHeader() throws Exception {
		stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
				.withRequestBody(WireMock.equalTo(""))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/md").headers(requestHeaders)
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andDo(MockMvcResultHandlers.print());
	}

	@org.junit.After
	public void tearDown() throws Exception {
		//WireMock.reset();
		wireMockServer.stop();

	}
}
