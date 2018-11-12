package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SchedulerIT extends BaseIT {


  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  private ObjectNode prepareSchedulerRequest() throws JsonProcessingException {
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
    ArrayNode emailNode = mapper.createArrayNode();
    emailNode.add("guest@guest.com");
    requestObject.put("emailList", emailNode);
    requestObject.put("endDate", simpleDateFormat.format(calendar.getTime()));
    return requestObject;

    // incomingRequest = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestObject);


  }

  private ObjectNode prepareScheduleKeysRequest() throws JsonProcessingException {
    ObjectNode keysObject = mapper.createObjectNode();
    keysObject.put("jobName", "test");
    keysObject.put("JobKey", "test");
    keysObject.put("groupName", "test");
    keysObject.put("categoryId", "test");

    return keysObject;
    // scheduleKeysRequest = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(keysObject);
  }

  private ObjectNode prepareJobRequest() throws JsonProcessingException {

    ObjectNode jobObject = mapper.createObjectNode();
    jobObject.put("jobName", "test");
    jobObject.put("groupName", "test");
    jobObject.put("categoryId", "test");

    return jobObject;
  }

  
  @Before
  public  void setup() throws Exception {
    
    this.scheduleJobTest();
  }
  
  @Test
  public void scheduleJobTest() throws Exception {

    given(authSpec).body(prepareSchedulerRequest()).when().post("/bisscheduler/schedule").then()
        .assertThat().statusCode(200);

  }

  @Test
  public void updateJobTest() throws Exception {
    given(authSpec).body(prepareSchedulerRequest()).when().post("/bisscheduler/update").then()
        .assertThat().statusCode(200);

  }

  @Test
  public void unscheduleJobTest() throws Exception {
    given(authSpec).body(prepareSchedulerRequest()).when().post("/bisscheduler/unschedule").then()
        .assertThat().statusCode(200);


  }

  @Test
  public void deleteJobTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/delete").then()
        .assertThat().statusCode(200);



  }

  @Test
  public void pauseJobTest() throws Exception {


    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/delete").then()
        .assertThat().statusCode(200);



  }

  @Test
  public void resumeJobTest() throws Exception {
    given(authSpec).body(prepareJobRequest()).when().post("/bisscheduler/delete").then()
        .assertThat().statusCode(200);



  }


  @Test
  public void getAllJobsTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/jobs").then()
        .assertThat().statusCode(200);

  }

  @Test
  public void getJobDetailsTest() throws Exception {

    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/fetchJob").then()
        .assertThat().statusCode(200);



  }

  @Test
  public void isJobRunningTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/isJobRunning")
        .then().assertThat().statusCode(200);



  }


  @Test
  public void getJobStateTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/jobState").then()
        .assertThat().statusCode(200).extract().response().body().jsonPath().getJsonObject("data")
        .equals("SCHEDULED");



  }


  @Test
  public void stopJobTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/stop").then()
        .assertThat().statusCode(200);


  }

  @Test
  public void startJobNowTest() throws Exception {
    given(authSpec).body(prepareScheduleKeysRequest()).when().post("/bisscheduler/start").then()
        .assertThat().statusCode(200);

  }
  
  /*
   * Delete schedule after 
   * completing test.
   */
  @After
  public  void tearDown() throws Exception {
    this.deleteJobTest();
  }
  

}
