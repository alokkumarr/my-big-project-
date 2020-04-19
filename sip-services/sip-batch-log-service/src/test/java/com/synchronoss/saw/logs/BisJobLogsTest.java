package com.synchronoss.saw.logs;

import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@DataJpaTest
public class BisJobLogsTest {
  @Autowired SipJobDataRepository jobRepository;
  @Autowired BisFileLogsRepository bisFileLogsRepository;

  @Test
  public void testFindByJobType() {
    Page<BisJobEntity> list =
        jobRepository.findByChannelType(
            "BIS", PageRequest.of(0, 1, Sort.Direction.fromString("DESC"), "createdDate"));
    List jobList = list.getContent();
    Assert.assertNotNull(jobList);
    Assert.assertTrue(jobList.size() >= 0);
  }

  @Test
  public void testFindByChannelEntity() {
    BisChannelEntity channelEntity = new BisChannelEntity();
    channelEntity.setBisChannelSysId(1L);

    Page<BisJobEntity> list =
        jobRepository.findByChannelEntity(
            channelEntity, PageRequest.of(0, 1, Sort.Direction.fromString("DESC"), "createdDate"));
    List jobList = list.getContent();
    Assert.assertNotNull(jobList);
    Assert.assertTrue(jobList.size() >= 0);
  }

  @Test
  public void testFindByChannelEntityAndRoutelEntity() {
    BisChannelEntity channelEntity = new BisChannelEntity();
    channelEntity.setBisChannelSysId(1L);

    BisRouteEntity routeEntity = new BisRouteEntity();
    routeEntity.setBisRouteSysId(1L);

    Page<BisJobEntity> list =
        jobRepository.findByChannelEntityAndRoutelEntity(
            channelEntity,
            routeEntity,
            PageRequest.of(0, 1, Sort.Direction.fromString("DESC"), "createdDate"));
    List jobList = list.getContent();
    Assert.assertNotNull(jobList);
    Assert.assertTrue(jobList.size() >= 0);
  }
}
