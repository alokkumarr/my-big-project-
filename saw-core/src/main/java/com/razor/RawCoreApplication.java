package com.razor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@Repository("com.razor.raw.core.dao.repository")
@SpringBootApplication
public class RawCoreApplication implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(RawCoreApplication.class, args);
    }

   /* @Autowired
    JdbcTemplate jdbcTemplate;*/
    
	@Override
	public void run(String... arg0) throws Exception {
		//System.out.println("------------------------------------------------------"+jdbcTemplate);
		
	}
}
