package com.synchronoss.saw.workbench.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkConfig {

	private static final Logger logger = LoggerFactory.getLogger(SparkConfig.class);
	public static Map<String, String> configParams = new HashMap<String, String>();
	SparkConf conf = new SparkConf();

	public static JavaSparkContext jsc = null;
	public static SparkConf sparkConfig = null;
	
	public SparkConfig() {
		this.initiProperties();
		
		logger.debug("#### Initializing spark context ######");
		sparkConfig = initSparkConf();
		jsc =  new JavaSparkContext(sparkConfig);
		
		
	    logger.debug("#### Setting librarires as class path for spark context ######");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.core-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-core-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-parser-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.meta-api-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-data-profiler-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-preview-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-component-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-ext-4.jar");
	    jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.saw.sip-workbench-executor-service-4.jar");
	    logger.debug("#### Manual class path settings completed!! ######");
	    

		logger.debug("#### Initializing spark context completed s######"+ this.jsc);
	}


	public void initiProperties() {
		logger.debug("Loading properties");
		Properties properties = new Properties();
		try {
			logger.debug("Reading file...."+ "src/main/resources/application.properties");
			InputStream inputStream = new FileInputStream(System.getProperty("config")+
					File.separator + "application.properties");
			properties.load(inputStream);

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		configParams.putAll(properties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
		//System.out.println(configParams);

	}

	public SparkConf initSparkConf() {
		logger.debug("################setting sparak parameters.....");
		final String LIB_PATH = "/opt/bda/sip-workbench-executor/lib/";
		String jars = LIB_PATH + "com.synchronoss.bda.core-4.jar," 
				+  LIB_PATH + "com.synchronoss.bda.xdf-core-4.jar" 
				+  LIB_PATH + "com.synchronoss.bda.xdf-parser-4.jar,"
				+  LIB_PATH + "com.synchronoss.bda.meta-api-4.jar,"
				+  LIB_PATH +"com.synchronoss.bda.xdf-data-profiler-4.jar,"
				+  LIB_PATH + "com.synchronoss.bda.xdf-preview-4.jar,"
				+  LIB_PATH + "com.synchronoss.bda.xdf-component-4.jar"
				+  LIB_PATH + "com.synchronoss.bda.xdf-ext-4.jar"
				+  LIB_PATH + "com.synchronoss.saw.sip-workbench-executor-service-4.jar";
		
		logger.debug("####spark.jars ####"+ jars);
		
		this.conf
				// .set("spark.driver.extraJavaOptions",
				//		 "Dlog4j.configuration=file:///opt/bda/sip-workbench-executor/log4j.properties")
				 //.set("spark.executor.extraJavaOptions",
				//		 "Dlog4j.configuration=file:///opt/bda/sip-workbench-executor/log4j.properties")
				 .set("spark.ui.enabled", "false")
				 .set("spark.sql.caseSensitive", "false")
				 .set("driver.memory",this.configParams.get("driver.memory"))
				// .set("spark.yarn.jars",this.sparkYarnJars)
				 .set("spark.master", this.configParams.get("spark.master"))
				 .set("spark.jars", jars)
				.set("spark.hadoop.yarn.resourcemanager.hostname",
						this.configParams.get("spark.hadoop.yarn.resourcemanager.hostname"))
				.set("spark.cores.max", this.configParams.get("spark.cores.max"))
				.set("spark.executor.cores", this.configParams.get("spark.cores.max"))
				.set("spark.sql.inMemoryColumnarStorage.compressed",
						this.configParams.get("spark.sql.inMemoryColumnarStorage.compressed"))
				.set("spark.sql.inMemoryColumnarStorage.batchSize",
						this.configParams.get("spark.sql.inMemoryColumnarStorage.batchSize"))
				//.set("spark.jars", "/opt/bda/sip-workbench-executor/lib/com.synchronoss.saw.sip-workbench-executor-service-4.jar")
				.set("spark.sql.caseSensitive", this.configParams.get("spark.sql.caseSensitive"))
				.setAppName(this.configParams.get("spark.app.name"));

		this.setIfPathExists(conf, "spark.executor.cores", this.configParams.get("spark.executor.cores"));
		setIfPathExists(conf, "spark.yarn.jars", this.configParams.get("spark.yarn.jars"));
		setIfPathExists(conf, "spark.yarn.archive", this.configParams.get("spark.yarn.archive"));
		setIfPathExists(conf, "spark.yarn.dist.archives", this.configParams.get("spark.yarn.dist.archives"));
		setIfPathExists(conf, "spark.executor.instances", this.configParams.get("spark.executor.instances"));
		
		
		
		
		
		
		//setIfPathExists(conf, "spark.driver.port", this.configParams.get("spark.driver.port"));
		//setIfPathExists(conf, "spark.driver.host", this.configParams.get("spark.driver.host"));
		//setIfPathExists(conf, "spark.driver.bindAddress", this.configParams.get("spark.driver.bindAddress"));
		//setIfPathExists(conf, "spark.driver.blockManager.port",
		//		this.configParams.get("spark.driver.blockManager.port"));
		// setIfPathExists(conf,"spark.yarn.archive",this.sparkyarnJarsArchive);
		logger.debug("################spark params setting completed....."+ this.conf);
		return conf;

	}

	private void setIfPathExists(SparkConf sparkConf, String sparkProperty, String cfg) {
		if (cfg != null && !cfg.isEmpty() && !cfg.equals("")) {
			logger.debug("Configuration path found, so setting Spark property: {}", sparkProperty);
			sparkConf.set(sparkProperty, cfg);
		} else {
			logger.debug("Configuration path not found" + sparkProperty);
		}
	}

	public void initJavaSparkContext() {
		this.jsc =  new JavaSparkContext(initSparkConf());
	}

}
