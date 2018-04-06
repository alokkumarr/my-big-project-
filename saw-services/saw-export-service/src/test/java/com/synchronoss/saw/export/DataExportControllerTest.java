package com.synchronoss.saw.export;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
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
import com.synchronoss.saw.export.controller.DataExportController;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@WebMvcTest(DataExportController.class)
@TestPropertySource(locations = "application-test.properties")
@ComponentScan(basePackages = {"com.synchronoss"})
public class DataExportControllerTest {

	@Rule
	public WireMockRule wireMockRule_1 = new WireMockRule(options().port(8090));
	WireMockServer wireMockServer = null;


	@Before
	public void setup() throws Exception {
		wireMockServer = new WireMockServer(options().port(9091));
		wireMockServer.start();
	}
    @Test
    public void contextLoads() {
    }

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ExportService exportService;

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Value("${analysis.service.host}")
	private String analysisUrl;

	private static final String ANALYSIS_ID = "123";

	@Test
	public void dataExportControllerTestWithAuthorizationHeader() throws Exception {
		stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
				.withRequestBody(WireMock.equalTo(""))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		requestHeaders.set("authorization", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("export/c8bac579-9eac-4ecb-b5ed-29e01a342729/executions/1677a4cd-09ff-4688-b08c-b84bb3655641/data")
		    .headers(requestHeaders)
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andDo(MockMvcResultHandlers.print());
	}
	
	
	@Test
	public void dataExportControllerTestWithoutAuthorizationHeader() throws Exception {
		stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
				.withRequestBody(WireMock.equalTo(""))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("export/c8bac579-9eac-4ecb-b5ed-29e01a342729/executions/1677a4cd-09ff-4688-b08c-b84bb3655641/data").
		    headers(requestHeaders)
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void dataDispatchControllerTest() throws Exception {
		stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
				.withRequestBody(WireMock.equalTo(""))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("dispatch/c8bac579-9eac-4ecb-b5ed-29e01a342729/executions/1677a4cd-09ff-4688-b08c-b84bb3655641/data").
				headers(requestHeaders)
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andDo(MockMvcResultHandlers.print());
	}

	@org.junit.After
	public void tearDown() throws Exception {
		//WireMock.reset();
		wireMockServer.stop();

	}

	@Test
	public void testAnalysis() throws Exception {
        /* Set up mock response */
		String json = objectMapper.writeValueAsString(mockAnalysisResponse());
		log.trace("Mock analysis schedules JSON: {}", json);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("host", "localhost");
		requestHeaders.set("authorization", "localhost");
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(analysisUrl + "md/?analysisId="+ANALYSIS_ID)
				.headers(requestHeaders).contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andDo(MockMvcResultHandlers.print());

	}

	private AnalysisMetaData mockAnalysisResponse() {
		List<RowField> rowFields = new ArrayList<>();
		List<ColumnField> columnFields = new ArrayList<>();
		List<DataField> dataFields = new ArrayList<>();

		RowField rowField = new RowField();
		rowField.setColumnName("country.keyword");
		rowField.setGroupInterval(null);
		rowField.setType(RowField.Type.STRING);

		ColumnField columnField = new ColumnField();
		columnField.setColumnName("product.keyword");
		columnField.setGroupInterval(null);
		columnField.setType(ColumnField.Type.STRING);

		DataField dataField = new DataField();
		dataField.setAggregate(DataField.Aggregate.SUM);
		dataField.setColumnName("units");
		dataField.setName("units");
		rowFields.add(rowField);
		columnFields.add(columnField);
		dataFields.add(dataField);
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.setRowFields(rowFields);
		sqlBuilder.setColumnFields(columnFields);
		sqlBuilder.setDataFields(dataFields);
		Analysis analysis = new Analysis();
		analysis.setSqlBuilder(sqlBuilder);
		List<Analysis> analyses = new ArrayList<>();
		analyses.add(analysis);
		AnalysisMetaData analysisMetaData = new AnalysisMetaData();
		analysisMetaData.setData(analyses);
		return analysisMetaData;
	}

	@Test
	public void testListAnalysis() {
		try {
			stubFor(post(urlEqualTo("/auth/validateToken")).withHeader("Content-Type", WireMock.equalTo("application/json"))
                    .withRequestBody(WireMock.equalTo(""))
                    .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{'jobGroup': 'TESTJOBGROUP'}")));
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.set("host", "localhost");
			requestHeaders.set("authorization", "localhost");
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/exports/listFTP")
                    .headers(requestHeaders)
                    .contentType(MediaType.APPLICATION_JSON);
			mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().string("{\"ftp\":[]}"))
					.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
					.andDo(MockMvcResultHandlers.print());
		} catch (Exception e) {
			// exception occurred
		}
	}
}
