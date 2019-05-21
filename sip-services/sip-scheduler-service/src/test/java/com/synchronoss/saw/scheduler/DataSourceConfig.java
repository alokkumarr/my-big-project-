package com.synchronoss.saw.scheduler;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

   
  @Bean
  public DataSource dataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
      dataSource.setUrl("jdbc:hsqldb:hsql//localhost/saw_scheduler");
      dataSource.setUsername("root");
      dataSource.setPassword("");

      return dataSource;
  }
}
