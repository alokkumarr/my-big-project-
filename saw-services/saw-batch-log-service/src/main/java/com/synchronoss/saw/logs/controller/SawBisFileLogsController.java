package com.synchronoss.saw.logs.controller;

import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/ingestion/batch")
public class SawBisFileLogsController {

  @Autowired
  private BisFileLogsRepository bisLogsRepository;

  @RequestMapping(value = "",method = RequestMethod.GET)
  public List<BisFileLog> retrieveAllLogs() {
    return this.bisLogsRepository.findAll();
  }
  
  @RequestMapping(value = "/logs/{id}",method = RequestMethod.GET)
  public BisFileLog retriveLogById(@PathVariable String id) {

    return this.bisLogsRepository.findByPid(id);
  }

}
