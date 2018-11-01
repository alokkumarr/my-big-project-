package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import com.synchronoss.saw.logs.repository.FileLogsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "/bisfilelogs")
public class BisFileLogsControllers {

  @Autowired
  private FileLogsRepository bisLogsRepository;

  @GetMapping
  public List<BisFileLogs> retrieveAllStudents() {
    return this.bisLogsRepository.findAll();
  }

}
