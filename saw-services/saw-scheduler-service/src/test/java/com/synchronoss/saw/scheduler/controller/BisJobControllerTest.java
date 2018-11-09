package com.synchronoss.saw.scheduler.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.scheduler.SAWSchedulerServiceApplication;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.saw.scheduler.service.JobService;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = BisJobControllerTest.class)
@ContextConfiguration(classes = SAWSchedulerServiceApplication.class)
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BisJobControllerTest {

	//@Autowired
	private MockMvc mockMvc;
	@Autowired
	JobService<BisSchedulerJobDetails> bisService;
	
	 String incomingRequest;
	 String scheduleKeysRequest;
	 @Autowired
	     BisJobController bisJobController;
	  
    @Before
    public  void setup() throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode requestObject = mapper.createObjectNode();
      requestObject.put("entityId", "123");
      requestObject.put("description", "abcd");
      requestObject.put("cronExpression", "0 0 12 * * ?");
      requestObject.put("fileType", "xml");
      requestObject.put("jobGroup", "test");
      requestObject.put("jobName", "test");
      requestObject.put("channelType", "test");
      requestObject.put("userFullName", "guest");
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      Calendar calendar = Calendar.getInstance();
      requestObject.put("jobScheduleTime", simpleDateFormat.format(new Date()));
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      ArrayNode  emailNode = mapper.createArrayNode();
      emailNode.add("guest@guest.com");
      requestObject.put("emailList", emailNode);
      requestObject.put("endDate", simpleDateFormat.format(calendar.getTime()));
      
        
      incomingRequest = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestObject);
      
      ObjectNode keysObject = mapper.createObjectNode();
      keysObject.put("jobName", "test");
      keysObject.put("JobKey", "test");
      keysObject.put("groupName", "test");
      keysObject.put("categoryId", "test");
      scheduleKeysRequest = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(keysObject);	
    	
    	 
      mockMvc = standaloneSetup(bisJobController).build();
    	 
      this.scheduleTest();

    }
	
	
	@Test
	public void scheduleTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/schedule")
				.accept(MediaType.APPLICATION_JSON).content(incomingRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
	}

	@Test
	public void updateTest() throws Exception {
		
		RequestBuilder updateRequestBuilder = MockMvcRequestBuilders.post("/bisscheduler/update")
				.accept(MediaType.APPLICATION_JSON).content(incomingRequest).contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(updateRequestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
	}
	@Test
	public void unscheduleTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/unschedule")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void deleteTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/delete")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void pauseTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/pause")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void resumeTest() throws Exception {
		String request = "{\"jobName\": \"test\",\"groupName\": \"test\",\"categoryId\": \"test\"}";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/resume")
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	
	@Test
	public void getAllJobsTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bisscheduler/jobs")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON)
		 .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void getJobDetailsTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/fetchJob")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void isJobRunningTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/isJobRunning")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	
	@Test
	public void getJobStateTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/jobState")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		Assert.assertEquals(response.getContentAsString(), "{\"statusCode\":200,\"data\":\"SCHEDULED\"}");
		
	}
	
	
	@Test
	public void stopJobTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/stop")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@Test
	public void startJobNowTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/start")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(response.getStatus(),200);
		
	}
	
	@After
    public  void tearDown() {
    	 try {
			this.deleteTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
