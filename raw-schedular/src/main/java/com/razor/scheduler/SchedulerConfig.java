package com.razor.scheduler;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.StringUtils;

import com.razor.scheduler.jobs.report.AutowiringSpringBeanJobFactory;

/**
 * 
 * @author surendra.rajaneni
 *
 */
@Configuration
public class SchedulerConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
	
	@Value("${quartz.properties.location}")
	private String quartzPropertiesPath;
	
	 @Bean
	    public JobFactory jobFactory(ApplicationContext applicationContext) {
	        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
	        jobFactory.setApplicationContext(applicationContext);
	        return jobFactory;
	    }
	 
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,JobFactory jobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
    	logger.debug(this.getClass().getName() + " loading quartzProperties starts - quartzPropertiesPath :"+quartzPropertiesPath);
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        if(quartzPropertiesPath.contains("classpath"))
        {
        	quartzPropertiesPath = "";
        }
        if(!StringUtils.isEmpty(quartzPropertiesPath))
        {
        	File file = new File(quartzPropertiesPath+File.separator+"quartz.properties");
        	FileSystemResource fileSystemResource = new FileSystemResource(file);
        	propertiesFactoryBean.setLocation(fileSystemResource);
        }
        else
        	propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        
        logger.debug(this.getClass().getName() + " loading quartzProperties ends");
        return propertiesFactoryBean.getObject();
    }
    
}
