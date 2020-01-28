package com.synchronoss.saw.workbench.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.synchronoss.saw.workbench.controller.WordCountController;

import sncr.xdf.parser.NGParser;


@Service
public class WordCountService {

	@Autowired
	JavaSparkContext sc;
	 private static final Logger logger = Logger.getLogger(WordCountController.class);

	public Map<String, Long> getCount(List<String> wordList) {
		System.out.print(logger.getLevel());
		sc.setLogLevel("DEBUG");
		JavaRDD<String> words = sc.parallelize(wordList);
		Map<String, Long> wordCounts = words.countByValue();
		logger.debug("### Total Count "+ wordCounts.size());
		NGParser parser = new NGParser();
		try {
			parser.initComponent(sc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wordCounts;
	}

}
