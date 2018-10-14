package com.synchronoss.saw.semantic;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.synchronoss.saw.semantic.controller.SAWSemanticController;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import com.synchronoss.saw.semantic.service.SemanticServiceImpl;


@RunWith(SpringRunner.class)
@WebMvcTest(value = SAWSemanticController.class, secure = false)
@TestPropertySource(locations = "application-test.properties")
public class SAWSemanticControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(SAWSemanticControllerTest.class);
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SemanticServiceImpl semanticService;
  private String requestCreatedSemanticJSON =
      "{\"customerCode\":\"SYNCHRONOSS\",\"module\":\"ANALYZE\",\"type\":\"esReport\",\"username\":\"sawadmin@synchronoss.com\",\"projectCode\":\"workbench\",\"metricName\":\"dataMungerUpdated\",\"parentDataSetIds\":[\"data1\",\"data2\"],\"esRepository\":{\"indexName\":\"att_subscr_actv_mnly_ss\",\"storageType\":\"ES\",\"type\":\"actv_m_record\"},\"artifacts\":[{\"artifactName\":\"F_SUBSCR_ACTV_MNLY_SS\",\"artifactPosition\":[20,1],\"columns\":[{\"columnName\":\"TOT_BILLED_UNIT_CALL_3G_EDGE\",\"displayName\":\"3G Edge Billed Unit Call\",\"table\":\"F_SUBSCR_ACTV_MNLY_SS\",\"type\":\"integer\",\"hide\":false,\"joinEligible\":false,\"filterEligible\":true},{\"columnName\":\"TOT_BILLED_UNIT_CALL_3G_UMTS\",\"displayName\":\"3G UMTS Billed Unit Call\",\"table\":\"F_SUBSCR_ACTV_MNLY_SS\",\"type\":\"integer\",\"hide\":false,\"joinEligible\":false,\"filterEligible\":true}],\"data\":[]}],\"groupByColumns\":[],\"sqlBuilder\":{\"booleanCriteria\":\"AND\",\"joins\":[],\"filters\":[{\"type\":\"date\",\"tableName\":\"F_SUBSCR_ACTV_MNLY_SS\",\"columnName\":\"MONTH_VALUE\",\"isRuntimeFilter\":false,\"isGlobalFilter\":false,\"model\":{\"preset\":\"NA\",\"gte\":\"2017-12-01 00:00:00\",\"lte\":\"2017-12-01 00:00:00\"}}],\"sorts\":[],\"dataFields\":[{\"columnName\":\"TOT_BILLED_UNIT_MOU\",\"type\":\"double\",\"name\":\"TOT_BILLED_UNIT_MOU\",\"aggregate\":\"sum\"},{\"columnName\":\"CLIENT_TYPE.keyword\",\"type\":\"string\"}]},\"supports\":[{\"label\":\"TABLES\",\"category\":\"table\",\"children\":[{\"label\":\"Pivot\",\"icon\":\"icon-pivot\",\"type\":\"table:pivot\"},{\"label\":\"Report\",\"icon\":\"icon-report\",\"type\":\"table:esReport\"}]},{\"label\":\"CHARTS\",\"category\":\"charts\",\"children\":[{\"label\":\"Column Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:column\"},{\"label\":\"Stacked Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:stack\"},{\"label\":\"Line Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:line\"},{\"label\":\"Bar Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:bar\"},{\"label\":\"Scatter Plot\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:scatter\"},{\"label\":\"Bubble Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:bubble\"}]}],\"saved\":true}";
  private String responseCreatedSemanticJSON =
      "{\"statusMessage\":\"Entity is created successfully\",\"_id\":\"372962f4-7236-4a94-9a77-282a119ee8b3::semanticDataSet::1526491639558\",\"createdBy\":\"sawadmin@synchronoss.com\",\"saved\":true,\"module\":\"ANALYZE\"}";
  private SemanticNode responseCreatedObjectSemantic = getSemantic(responseCreatedSemanticJSON);

  private String responseReadSemanticJSON = requestCreatedSemanticJSON;
  private SemanticNode responseReadObjectSemantic = getSemantic(responseReadSemanticJSON);



  @Test
  public void createSemanticTestCase() throws Exception {
    Mockito.when(semanticService.addSemantic(Mockito.any(SemanticNode.class)))
        .thenReturn(responseCreatedObjectSemantic);
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/internal/semantic/workbench/create").accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .content(requestCreatedSemanticJSON).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(HttpHeaders.LOCATION, "http://localhost/internal/semantic/workbench/create");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @Test
  public void readSemanticTestCase() throws Exception {
    Mockito.when(semanticService.readSemantic(Mockito.any(SemanticNode.class)))
        .thenReturn(responseReadObjectSemantic);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/internal/semantic/workbench/372962f4-7236-4a94-9a77-282a119ee8b3::semanticDataSet::1526491639558")
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(HttpHeaders.LOCATION, "http://localhost/internal/semantic/workbench/create");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void filterSemanticTestCase() throws Exception {
    Mockito.when(semanticService.search(Mockito.any(SemanticNode.class)))
        .thenReturn(getSemanticList(responseReadSemanticJSON));
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/internal/semantic/workbench/filter?username=sawadmin@synchronoss.com&module=ANALYZE")
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).header(HttpHeaders.LOCATION,
            "http://localhost/internal/semantic/workbench/filter?username=sawadmin@synchronoss.com&module=ANALYZE");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
  }

  public SemanticNode getSemantic(String semanticJSONString) {
    SemanticNode mockObserve = null;
    try {
      mockObserve = new ObjectMapper().readValue(semanticJSONString, SemanticNode.class);
    } catch (IOException e) {
      logger.error(e.getCause().toString());
    }
    return mockObserve;
  }

  public SemanticNodes getSemanticList(String semanticJSONString) {
    SemanticNodes nodes = new SemanticNodes();
    ArrayList<SemanticNode> responseFilterObjectSemanticList =
        new ArrayList<SemanticNode>(Arrays.asList(responseReadObjectSemantic));
    nodes.setSemanticNodes(responseFilterObjectSemanticList);
    return nodes;
  }
}
