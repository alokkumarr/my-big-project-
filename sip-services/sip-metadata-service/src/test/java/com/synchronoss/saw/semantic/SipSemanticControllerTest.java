package com.synchronoss.saw.semantic;

import static org.junit.Assert.assertEquals;

import com.synchronoss.saw.MetadataTestUtils;
import com.synchronoss.saw.semantic.controller.SipSemanticController;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import com.synchronoss.saw.semantic.service.SemanticServiceImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
@WebMvcTest(value = SipSemanticController.class, secure = false)
public class SipSemanticControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(SipSemanticControllerTest.class);
  @Autowired private MockMvc mockMvc;

  @MockBean private SemanticServiceImpl semanticService;
  private String requestCreatedSemanticJson =
      MetadataTestUtils.getJsonString("com/synchronoss/saw/semantic/RequestSemantic.json");

  private String responseCreatedSemanticJson =
      MetadataTestUtils.getJsonString("com/synchronoss/saw/semantic/ResponseSemantic.json");

  private SemanticNode responseCreatedObjectSemantic = getSemantic(responseCreatedSemanticJson);

  private String responseReadSemanticJson = requestCreatedSemanticJson;
  private SemanticNode responseReadObjectSemantic = getSemantic(responseReadSemanticJson);

  @Test
  public void createSemanticTestCase() throws Exception {
    Mockito.when(semanticService.addSemantic(Mockito.any(SemanticNode.class)))
        .thenReturn(responseCreatedObjectSemantic);
    Mockito.when(semanticService.addDskToSipSecurity(Mockito.any(SemanticNode.class),
        Mockito.any(HttpServletRequest.class)))
        .thenReturn(true);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/internal/semantic/workbench/create")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(requestCreatedSemanticJson)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header(HttpHeaders.LOCATION, "http://localhost/internal/semantic/workbench/create");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @Test
  public void readSemanticTestCase() throws Exception {
    Mockito.when(semanticService.readSemantic(Mockito.any(SemanticNode.class)))
        .thenReturn(responseReadObjectSemantic);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(
                "/internal/semantic/workbench/372962f4-7236-4a94-9a77-282a119ee8b3"
                    + "::semanticDataSet::1526491639558")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header(HttpHeaders.LOCATION, "http://localhost/internal/semantic/workbench/create");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void filterSemanticTestCase() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(
                "/internal/semantic/workbench/filter"
                    + "?username=sawadmin@synchronoss.com&module=ANALYZE")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header(
                HttpHeaders.LOCATION,
                "http://localhost/internal/semantic/workbench/filter?username=sawadmin@synchronoss.com&module=ANALYZE");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  /**
   * Get Semantic Node Object.
   *
   * @param semanticJsonString SemanticNode string.
   * @return SemanticNode
   */
  public SemanticNode getSemantic(String semanticJsonString) {
    SemanticNode mockObserve = null;
    try {
      mockObserve = new ObjectMapper().readValue(semanticJsonString, SemanticNode.class);
    } catch (IOException e) {
      logger.error(e.getCause().toString());
    }
    return mockObserve;
  }

  /**
   * getSemanticList.
   *
   * @param semanticJsonString SemanticNode string.
   * @return
   */
  public SemanticNodes getSemanticList(String semanticJsonString) {
    SemanticNodes nodes = new SemanticNodes();
    ArrayList<SemanticNode> responseFilterObjectSemanticList =
        new ArrayList<SemanticNode>(Arrays.asList(responseReadObjectSemantic));
    nodes.setSemanticNodes(responseFilterObjectSemanticList);
    return nodes;
  }
}
