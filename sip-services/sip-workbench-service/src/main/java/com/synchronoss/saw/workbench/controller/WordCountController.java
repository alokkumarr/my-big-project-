package com.synchronoss.saw.workbench.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.workbench.service.WordCountService;




@RestController
public class WordCountController {

	@Autowired
	WordCountService service;
	
	   private static final Logger logger = Logger.getLogger(WordCountController.class.getName());

	@RequestMapping(method = RequestMethod.POST, path = "/wordcount")
	public Map<String, Long> count(@RequestParam(required = false) String words) {
		System.out.print(logger.getLevel());
		logger.info("###Entered controller###");
		List<String> wordList = Arrays.asList(words.split("\\|"));
		return service.getCount(wordList);
	}

}
