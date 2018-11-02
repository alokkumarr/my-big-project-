package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import com.synchronoss.saw.logs.repository.FileLogsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "/bisfilelogs")
public class BisFileLogsController {

  @Autowired
  private FileLogsRepository bisLogsRepository;

  @RequestMapping(value ="",method = RequestMethod.GET)
  public List<BisFileLogs> retrieveAllLogs() {
    return this.bisLogsRepository.findAll();
  }
  
  @RequestMapping(value ="/{id}",method = RequestMethod.GET)
  public BisFileLogs retriveLogById(@PathVariable String id) {

    return this.bisLogsRepository.findByPid(id);
  }

}
