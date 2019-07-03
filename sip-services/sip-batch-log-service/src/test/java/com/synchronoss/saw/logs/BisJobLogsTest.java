package com.synchronoss.saw.logs;

import com.synchronoss.saw.logs.models.JobDetails;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BisJobLogsTest {
  @Autowired
  SipJobDataRepository jobRepository;

  @Test
  public void testFindByJobType() {
    Page<JobDetails> list = jobRepository.findByChannelType("SFTP",
        PageRequest.of(0, 1), "DESC", "createdDate");
    List jobList = list.getContent();
    Assert.assertNotNull(jobList);
    Assert.assertTrue(jobList.size()>=0);
  }

}
