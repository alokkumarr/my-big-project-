package com.synchronoss.saw.logs.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import com.synchronoss.saw.logs.repository.BISFileLogsRepository;


@RestController
@RequestMapping(value = "/bisfilelogs")
public class BisFileLogsControllers {

	@Autowired
	private BISFileLogsRepository bisLogsRepository;
	
	
	@GetMapping
	public List<BisFileLogs> retrieveAllStudents() {
		return this.bisLogsRepository.findAll();
	}
	
	
}
