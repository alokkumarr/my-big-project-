package com.synchronoss.saw.scheduler.job;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;

@RunWith(SpringRunner.class)
public class BisSimpleJobTest {


  @Mock
  RetryTemplate retryTemplate;

  @Mock
  RestTemplate restTemplate;

  @InjectMocks
  @Spy
  BisSimpleJob simpleJob;

  @Test
  public void testExecuteInternal()
      throws RestClientException, URISyntaxException, JobExecutionException {
    JobExecutionContext context = Mockito.mock(JobExecutionContext.class);
    JobDetail jobDetail = Mockito.mock(JobDetail.class);
    JobKey jobKey = new JobKey("test");
    JobDataMap jobDataMap = Mockito.mock(JobDataMap.class);
    ReflectionTestUtils.setField(simpleJob, "bisTransferUrl", "http://testcron");
    BisSchedulerJobDetails bisJobDetails = new BisSchedulerJobDetails();
    Mockito.when(restTemplate.postForLocation(Mockito.anyString(),
        Mockito.any(BisSchedulerJobDetails.class))).thenReturn(new URI(""));


    when(context.getJobDetail()).thenReturn(jobDetail);
    when(jobDetail.getKey()).thenReturn(jobKey);
    when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
    when(jobDataMap.get("JOB_DATA_MAP")).thenReturn(bisJobDetails);
    when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
    when(jobDataMap.getString("myKey")).thenReturn("test");
    simpleJob.executeInternal(context);

    verify(context).getJobDetail();
    verify(jobDetail).getKey();
    verify(jobDetail).getJobDataMap();
  }

  @Test
  public void testInterrupt() {
    BisCronJob cronJob = new BisCronJob();
    try {
      cronJob.interrupt();
    } catch (UnableToInterruptJobException e) {
      Assert.assertEquals(e.getMessage(), "UnableToInterruptJobException");

    }
  }


}
