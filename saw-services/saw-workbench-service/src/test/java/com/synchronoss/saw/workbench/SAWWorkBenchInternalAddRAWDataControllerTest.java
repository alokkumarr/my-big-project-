package com.synchronoss.saw.workbench;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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

import com.synchronoss.saw.workbench.controller.SAWWorkBenchInternalAddRAWDataController;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import com.synchronoss.saw.workbench.service.SAWWorkbenchServiceImpl;


@RunWith(SpringRunner.class)
@WebMvcTest(value = SAWWorkBenchInternalAddRAWDataController.class, secure = false)
@TestPropertySource(locations = "application-test.properties")
public class SAWWorkBenchInternalAddRAWDataControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(SAWWorkBenchInternalAddRAWDataControllerTest.class);
  @Autowired
  private MockMvc mockMvc;
  
  @MockBean
  private SAWWorkbenchServiceImpl  workBenchService;
  private String responseInspectJSON = "{\"lineSeparator\":\"\\n\",\"delimiter\":\",\",\"quoteChar\":\"'\",\"quoteEscapeChar\":\"\\\\\",\"headerSize\":1,\"fieldNamesLine\":1,\"rowsToInspect\":10000,\"delimiterType\":\"delimited\",\"description\":\"It's delimited file inspecting to verify & understand the content of file\",\"fields\":[{\"name\":\"DOJ\",\"type\":\"date\",\"format\":[\"YYYY-MM-DD\"]},{\"name\":\"Name\",\"type\":\"string\"},{\"name\":\"ID\",\"type\":\"long\"},{\"name\":\"ZipCode\",\"type\":\"long\"},{\"name\":\"Salary\",\"type\":\"double\"},{\"name\":\"Resignation\",\"type\":\"string\",\"format\":[\"YYYY-MMM-DD HH:mm:ss\",\"MM-DD-YYYY\"]}],\"info\":{\"totalLines\":5,\"dataRows\":4,\"maxFields\":6,\"minFields\":6,\"file\":\"test.csv\"},\"samplesParsed\":[{\"DOJ\":\"2018-01-27\",\"Name\":\"Saurav Paul\",\"ID\":\"213\",\"ZipCode\":\"20191\",\"Salary\":\"54.56\",\"Resignation\":\"01-27-2018\"},{\"DOJ\":\"2018-01-27\",\"Name\":\"Alexey Sorokin\",\"ID\":\"215\",\"ZipCode\":\"20191\",\"Salary\":\"84.70\",\"Resignation\":\"Alexey has resigned & his last day was on 2018-Jan-31 15:08:00\"},{\"DOJ\":\"Saurav has joined SNCR on 2015-07-15\",\"Name\":\"Saurav Paul\",\"ID\":\"213\",\"ZipCode\":\"20191\",\"Salary\":\"99.70\",\"Resignation\":\"01-27-2018\"},{\"DOJ\":\"2018-01-27\",\"Name\":\"Saurav Paul\",\"ID\":\"213\",\"ZipCode\":\"20191\",\"Salary\":\"23.40\",\"Resignation\":\"01-27-2018\"}]}";
  private String responsePreviewJSON = "{\"projectId\":\"project2\",\"path\":\"*.csv\",\"data\":[\"DOJ,Name,ID,ZipCode,Salary,Resignation\\r\",\"2018-01-27,Saurav Paul,213,20191,23.40,01-27-2018\\r\",\"2018-01-27,Saurav Paul,213,20191,54.56,01-27-2018\\r\",\"2018-01-27,Alexey Sorokin,215,20191,84.70,Alexey has resigned & his last day was on 2018-Jan-31 15:08:00\\r\",\"Saurav has joined SNCR on 2015-07-15,Saurav Paul,213,20191,99.70,01-27-2018\\r\"]}";
  private Inspect inspect = getInspect(responseInspectJSON);
  private Project project = getProject(responsePreviewJSON);
  private String requestPreviewJSON = "{\"path\":\"test.csv\"}";
  private String requestInspectJSON = "{\"file\":\"test.csv\",\"lineSeparator\":\"\\n\",\"delimiter\":\",\",\"quoteChar\":\"'\",\"quoteEscapeChar\":\"\\\\\",\"headerSize\":1,\"fieldNamesLine\":1,\"dateFormats\":[],\"rowsToInspect\":5,\"delimiterType\":\"delimited\"}";
  
  
  @Test
  public void previewRAWData() throws Exception {
      Mockito.when(
          workBenchService.previewFromProjectDirectoybyId(Mockito.any(Project.class))).
          thenReturn(project);
      RequestBuilder requestBuilder = MockMvcRequestBuilders
              .post("/internal/workbench/projects/project2/raw/directory/preview")
              .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .content(requestPreviewJSON)
              .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .header(HttpHeaders.LOCATION, "http://localhost/internal/workbench/projects/project2/raw/directory/preview");
      MvcResult result = mockMvc.perform(requestBuilder).andReturn();
      MockHttpServletResponse response = result.getResponse();
      assertEquals(HttpStatus.OK.value(), response.getStatus());
  } 
 
  
  @Test
  public void inspectRAWData() throws Exception {
      Mockito.when(
          workBenchService.inspectFromProjectDirectoybyId(Mockito.any(Inspect.class))).
          thenReturn(inspect);
      RequestBuilder requestBuilder = MockMvcRequestBuilders
              .post("/internal/workbench/projects/project2/raw/directory/inspect")
              .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .content(requestInspectJSON)
              .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .header(HttpHeaders.LOCATION, "http://localhost/internal/workbench/projects/project2/raw/directory/inspect");
      MvcResult result = mockMvc.perform(requestBuilder).andReturn();
      MockHttpServletResponse response = result.getResponse();
      assertEquals(HttpStatus.OK.value(), response.getStatus());
  } 
  
   public Inspect getInspect(String inspectString) 
  {
    Inspect mockObserve = null;
    try {
      mockObserve = new ObjectMapper().readValue(inspectString, Inspect.class);
    } catch (IOException e) {
      logger.error("Error ",e);
    }
    return mockObserve;
  }
  
  public Project getProject(String projectString) 
  {
    Project mockObserve = null;
    try {
      mockObserve = new ObjectMapper().readValue(projectString, Project.class);
    } catch (IOException e) {
      logger.error(e.getCause().toString());
    }
    return mockObserve;
  }
}
