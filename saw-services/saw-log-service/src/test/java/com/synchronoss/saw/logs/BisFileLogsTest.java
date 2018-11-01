package com.synchronoss.saw.logs;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import com.synchronoss.saw.logs.repository.FileLogsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;




@RunWith(SpringRunner.class)
@DataJpaTest
//@SpringBootTest(classes={SawLogServiceApplication.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
@ActiveProfiles("test")
//@TestPropertySource(locations="classpath:application-test.properties")
//(replace = Replace.NONE)
public class BisFileLogsTest {
    @Autowired
    private FileLogsRepository bisLogsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testiFndAll() {
        Iterable<BisFileLogs> items = this.bisLogsRepository.findAll();

        for (BisFileLogs bisFileLogs : items) {
            System.out.println("$$$$$$" + bisFileLogs.getFileName());

        }

        // BisFileLogs log = this.bisLogsRepository.findByPid("2");
        // System.out.println(log.getFileName());
    }

    @Test
    public void testFindByFileName() {
        System.out.println("$$$$$$" + this.bisLogsRepository.isFileNameExists("TEST"));
    }

}
