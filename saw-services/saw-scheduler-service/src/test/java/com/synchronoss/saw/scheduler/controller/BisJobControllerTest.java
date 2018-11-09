package com.synchronoss.saw.scheduler.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.synchronoss.saw.scheduler.SAWSchedulerServiceApplication;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.saw.scheduler.service.JobService;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = BisJobControllerTest.class)
@ContextConfiguration(classes = SAWSchedulerServiceApplication.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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
    	 incomingRequest = "{\"entityId\": \"123\",\"description\":"
    			+ " \"dd\",\"cronExpression\": \"0 0 12 * * ?\","
    			+ "\"emailList\":[ \"Ford\", \"BMW\", \"Fiat\" ],"
    			+ "\"fileType\": \"xml\",\"jobGroup\": \"test\","
    			+ "\"jobName\": \"test\",\"jobScheduleTime\": "
    			+ "\"2018-11-29T05:06\",\"channelType\": \"test\","
    			+ "\"userFullName\": \"naresh\",\"endDate\": "
    			+ "\"2018-11-30T06:06\"}";
    	
    	 scheduleKeysRequest = "{\"jobName\": \"test\",\"JobKey\":\"test\",\"groupName\":"
    			+ " \"test\",\"categoryId\": \"test\"}";
    	 
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
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/bisscheduler/jobs")
				.accept(MediaType.APPLICATION_JSON).content(scheduleKeysRequest).contentType(MediaType.APPLICATION_JSON);
		// .header(HttpHeaders.LOCATION, "http://localhost/observe/dashboards/create");
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
