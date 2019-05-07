package com.synchronoss.saw.scheduler;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import com.synchronoss.saw.scheduler.service.AnalysisService;
import com.synchronoss.saw.scheduler.service.AnalysisServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(AnalysisService.class)
@TestPropertySource(properties = {"saw-analysis-service-url=http://localhost/analysis",})
@ContextConfiguration(classes = {DataSourceConfig.class})
public class AnalysisServiceTest {
  private static final String ANALYSIS_ID = "123";
  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  @Value("${saw-analysis-service-url}")
  private String analysisUrl;

  @InjectMocks
  @Spy
  private AnalysisServiceImpl service;
  
  @Mock
  RestTemplate restTemplate;

  @Autowired
  private MockRestServiceServer server;

  @Before
  public void setUp() {
      server = MockRestServiceServer.createServer(restTemplate);
  }
  
  @Test
  public void testAnalysisExecute() throws Exception {
    /* Set up mock response */
    String json = "{}";
    log.trace("Mock execute analysis JSON: {}", json);
    server.expect(requestTo(analysisUrl + "/123/executions"));
    // .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    /* Execute analysis */
    service.executeAnalysis(ANALYSIS_ID);
  }
}
