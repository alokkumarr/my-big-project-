package com.synchronoss.saw.logs;

import com.synchronoss.saw.logs.repository.SipJobDataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BisJobLogsTest {
  @Autowired SipJobDataRepository jobRepository;

  @Test
  public void testFindByJobType() {
    //    Page<JobDetails> list = jobRepository.findByChannelType("SFTP",
    //        PageRequest.of(0, 1), "DESC", "createdDate");
    //    List jobList = list.getContent();
    //    Assert.assertNotNull(jobList);
    //    Assert.assertTrue(jobList.size()>=0);

    /**
     * Note : Note that neither Hibernate nor Spring Data validate native queries at startup. Since
     * the query may contain database-specific SQL, there’s no way Spring Data or Hibernate can know
     * what to check for. So, these tests are handled directly in integration
     * testing(com/synchronoss/saw/BatchIngestionIT.java).
     * For more detailed description : Please read the following documentation :
     * https://reflectoring.io/spring-boot-data-jpa-test/
     *
     * <p>The prime reason for using Hibernate-native Query over JPA is : Spring JPA doesn't provide
     * any methods to extract a field, which is stored as JSON format in mariaDB.
     * So to extract a field from JSON, we choose using native query.
     */
  }
}
