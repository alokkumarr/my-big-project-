package com.synchronoss.saw.observe;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.MetadataTestUtils;
import com.synchronoss.saw.observe.controller.ObserveController;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.service.ObserveServiceImpl;
import com.synchronoss.sip.utils.SipCommonUtils;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ObserveController.class, secure = false)
public class ObserveControllerRestTest {

  private static final Logger logger = LoggerFactory.getLogger(ObserveControllerRestTest.class);
  @Autowired private MockMvc mockMvc;

  @MockBean private ObserveServiceImpl observeService;

  private String jsonStringStore =
      MetadataTestUtils.getJsonString("com/synchronoss/saw/observe/ObserveStore.json");

  private String incomingJsonRequest =
      MetadataTestUtils.getJsonString("com/synchronoss/saw/observe/DashBoardJson.json");
  private Observe observe = getObserve(jsonStringStore);

  @Test
  public void createDashboard() throws Exception {

    Mockito.when(observeService.addDashboard(Mockito.any(Observe.class)))
        .thenReturn(ObserveUtils.prepareResponse(observe, "Entity is created successfully"));
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/observe/dashboards/create")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(incomingJsonRequest)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  public void retrieveDashboard() throws Exception {
    Mockito.when(observeService.getDashboardbyCriteria(Mockito.any(Observe.class)))
        .thenReturn(
            ObserveUtils.prepareResponse(observe, "Entity has been retrieved successfully"));
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/observe/dashboards/id:portalDataSet::201")
            .accept(MediaType.APPLICATION_JSON);
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    logger.info("Response code {}", result.getResponse());
    String expected =
        getObserveResponseString(
            ObserveUtils.prepareResponse(observe,
                "UNAUTHORIZED ACCESS : User don't have the Access privileges for dashboard!!"));
    JSONAssert.assertEquals(result.getResponse().getContentAsString(), expected, false);
  }

  @Test
  public void updateDashboard() throws Exception {
    Mockito.when(observeService.updateDashboard(Mockito.any(Observe.class)))
        .thenReturn(ObserveUtils.prepareResponse(observe, "Entity is updated successfully"));
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.put("/observe/dashboards/update/id:portalDataSet::201")
            .accept(MediaType.APPLICATION_JSON)
            .content(incomingJsonRequest)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    logger.info("Response code {}", result.getResponse());
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  /**
   * getObserve.
   *
   * @param observeString string to get Observe Object.
   * @return Observe
   */
  public Observe getObserve(String observeString) {
    String sanitizedObserveString = SipCommonUtils.sanitizeJson(observeString);
    Observe mockObserve = null;
    try {
      mockObserve = new ObjectMapper().readValue(sanitizedObserveString, Observe.class);
    } catch (IOException e) {
      logger.error(e.getCause().toString());
    }
    return mockObserve;
  }

  /**
   * getObserveResponseString.
   *
   * @param observeString to get ObserveResponseString
   * @return String
   */
  public String getObserveResponseString(ObserveResponse observeString) {
    String mockObserveResponse = null;
    try {
      mockObserveResponse = new ObjectMapper().writeValueAsString(observeString);
    } catch (IOException e) {
      logger.error(e.getCause().toString());
    }
    return mockObserveResponse;
  }
}
