package com.synchronoss.saw.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.scheduler.service.AnalysisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(AnalysisService.class)
@TestPropertySource(properties = {
    "saw-analysis-service-url=http://localhost/analysis",
})
public class AnalysisServiceTest {
    private static final String ANALYSIS_ID = "123";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    @Value("${saw-analysis-service-url}")
    private String analysisUrl;
    @Autowired
    private AnalysisService service;
    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testAnalysisExecute() {
        /* Set up mock response */
        String json = "{}";
        log.trace("Mock execute analysis JSON: {}", json);
        server.expect(requestTo(analysisUrl + "/123/executions"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        /* Execute analysis */
        service.executeAnalysis(ANALYSIS_ID);
    }
}
