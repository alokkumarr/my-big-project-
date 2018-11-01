package com.synchronoss.saw.logs;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.synchronoss.saw.logs.repository.FileLogsRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
@ActiveProfiles("test")
public class BisFileLogsTest {
    @Autowired
    private FileLogsRepository bisLogsRepository;

    /**
     * Tests findAll method from repository
     */
    @Test
    public void testiFndAll() {
        Assert.assertEquals(this.bisLogsRepository.findAll().size(), 1);
    }

    /**
     * Tests file name exists or not in the logs
     */
    @Test
    public void testFindByFileName() {
        Assert.assertTrue(this.bisLogsRepository.isFileNameExists("TEST"));
        Assert.assertFalse(this.bisLogsRepository.isFileNameExists("xyz"));

    }
    
    /**
     * Tests log entry with given pid exists or not
     */
    @Test
    public void testFindByPid() {
        Assert.assertNotNull(this.bisLogsRepository.findByPid("2"));
        Assert.assertNull(this.bisLogsRepository.findByPid(null));

    }

}
