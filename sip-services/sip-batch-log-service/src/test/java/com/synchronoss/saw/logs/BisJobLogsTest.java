package com.synchronoss.saw.logs;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BisJobLogsTest {
  @Autowired
  SipJobDataRepository jobRepository;
  
  @Test
  public void testFindByJobType() {
    List<BisJobEntity> list = jobRepository.findByChannelType("BIS",
        PageRequest.of(0, 1, Sort.Direction.fromString("DESC"), "createdDate"));
    Assert.assertNotNull(list);
    Assert.assertTrue(list.size()>0);
  }

}
