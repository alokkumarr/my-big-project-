package com.synchronoss.saw.workbench;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.info.BuildInfoContributor;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {
	
	 
	private static final Logger logger = LoggerFactory.getLogger(SparkConfig.class);
	
	
	@Value("${workbench.app-name}")
	private String appName;
	

	
	//@Value("${spark.home}")
	//private String sparkHome;
	
	@Value("${spark.executor.memory}")
	private String sparkExecutorMemory;
	
	@Value("${spark.cores.max}")
	private String sparkCoresMax;
	
	@Value("${spark.executor.cores}")
	private String sparkExecutorCores;
	
	@Value("${spark.driver.memory}")
	private String driverMemory;
	
	@Value("${spark.hadoop.yarn.resourcemanager.hostname}")
	private String hadoopRMHostName;
	
	@Value("${spark.yarn.jars:@null}")
	private String sparkYarnJars ;
	
	@Value("${spark.yarn.archive:@null}")
	private String sparkyarnJarsArchive;
	
	@Value("${spark.executor.instances}")
	private String sparkExecutorInstances;
	
	@Value("${spark.driver.port}")
	private String sparkDriverPort;
	
	@Value("${spark.driver.host}")
	private String sparkDriverHost;
	
	
	@Value("${spark.driver.bindAddress}")
	private String sparkDriverBindAddress;
	
	@Value("${spark.sql.inMemoryColumnarStorage.compressed}")
	private String sparkInMemoryColStorageCompressed;
	
	@Value("${spark.sql.inMemoryColumnarStorage.batchSize}")
	private String sparkSqlInMemoryBatchSize;
	
	@Value("${spark.sql.caseSensitive}")
	private String sparkSqlCaseSensitive = "false";
	
	@Value("${spark.driver.blockManager.port}")
	private String sparkDriverBlockManagerPort;
	
	@Value("${spark.master}")
	private String sparkMaster;
	
	@Value("${workbench.lib.path}")
	private String libPath;
	

	//BuildProperties buildProperties = this.context.getBean(BuildProperties.class);
	@Autowired
	private BuildProperties buildProp;
	
	 @Autowired
	  private ApplicationContext appContext;


	@Bean
	public SparkConf conf() {
		logger.info("######Spark yarn jars from path $#####"+ this.sparkYarnJars);
		SparkConf conf=  new SparkConf()
				//.set("spark.driver.extraJavaOptions", "Dlog4j.configuration=file://src/main/resources/log4j.properties")
				//.set("spark.executor.extraJavaOptions","Dlog4j.configuration=file://src/main/resources/log4j.properties")
				//.set("spark.yarn.jars",this.sparkYarnJars)
				//.set("SPARK_YARN_MODE", "true")
				.set("spark.hadoop.yarn.resourcemanager.hostname", this.hadoopRMHostName)
				.set("spark.cores.max",this.sparkCoresMax)
				.set("spark.sql.inMemoryColumnarStorage.compressed", this.sparkInMemoryColStorageCompressed)
		        .set("spark.sql.inMemoryColumnarStorage.batchSize", this.sparkSqlInMemoryBatchSize)
		        .set("spark.sql.caseSensitive", this.sparkSqlCaseSensitive)
				.setMaster(this.sparkMaster)
				.setAppName(this.appName);
		
		
		        this.setIfPathExists(conf, "spark.executor.cores", this.sparkExecutorCores);
		        setIfPathExists(conf,"spark.yarn.jars", this.sparkYarnJars);
		        setIfPathExists(conf,"spark.yarn.archive", this.sparkyarnJarsArchive);
		        setIfPathExists(conf,"spark.executor.instances", this.sparkExecutorInstances);
		        setIfPathExists(conf,"spark.driver.port", this.sparkDriverPort);
		        setIfPathExists(conf,"spark.driver.host", this.sparkDriverHost);
		        setIfPathExists(conf,"spark.driver.bindAddress", this.sparkDriverBindAddress);
		        setIfPathExists(conf,"spark.driver.blockManager.port", this.sparkDriverBlockManagerPort);
		        //setIfPathExists(conf,"spark.yarn.archive",this.sparkyarnJarsArchive);
		        
		        return conf;
		        
	}
	
	private void setIfPathExists(SparkConf sparkConf, String sparkProperty, String cfg  ) {
	    if (!cfg.isEmpty() && !cfg.equals("")) {
	      logger.debug("Configuration path found, so setting Spark property: {}", sparkProperty);
	      sparkConf.set(sparkProperty, cfg);
	    } else {
	      logger.debug("Configuration path not found" + sparkProperty);
	    }
	  }

	@Bean
	public JavaSparkContext sc() {
		BuildProperties buildProperties = this.appContext.getBean(BuildProperties.class);
		 String JAR_SUFFIX = "-"+buildProperties.getVersion() + ".jar";
	JavaSparkContext jsc = new  JavaSparkContext(SparkContext.getOrCreate(conf()));
	logger.debug("#### Setting librarires as class path for spark context ######");
    jsc.addJar(libPath + "com.synchronoss.bda.core"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-core"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-parser"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.meta-api"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-data-profiler"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-preview"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-component"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.bda.xdf-ext"+ JAR_SUFFIX);
    jsc.addJar(libPath + "com.synchronoss.saw.sip-workbench-service"+ JAR_SUFFIX);
    logger.debug("#### Manual class path settings completed!! ######");
    
    return jsc;
	}
	
	
	@Bean
	@ConditionalOnEnabledInfoContributor("build")
	@ConditionalOnSingleCandidate(BuildProperties.class)
	//@Order(DEFAULT_ORDER)
	public InfoContributor buildInfoContributor(BuildProperties buildProperties) {
		return new BuildInfoContributor(buildProperties);
	}
	
	
}
