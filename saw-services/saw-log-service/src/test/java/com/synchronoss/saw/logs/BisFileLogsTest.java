package com.synchronoss.saw.logs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import com.synchronoss.saw.logs.repository.BISFileLogsRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
//@SpringBootTest(classes={SawLogServiceApplication.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BisFileLogsTest {
	@Autowired
	private BISFileLogsRepository bisLogsRepository;
	
	@Autowired
    private TestEntityManager entityManager; 
	
	
	@Test
	public void findAll() {
		Iterable<BisFileLogs> items = this.bisLogsRepository.findAll();
		
		for (BisFileLogs bisFileLogs:items) {
			System.out.println("$$$$$$" + bisFileLogs.getFileName());
			
		}
		
		BisFileLogs log = this.bisLogsRepository.findByPid("2");
		System.out.println(log.getFileName());
	}

	
	
}
