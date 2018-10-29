package com.synchronoss.saw.logs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.synchronoss.saw.logs.entities.BisFileLogs;
@Repository
public interface BISFileLogsRepository extends JpaRepository<BisFileLogs, Integer>{
	
	BisFileLogs findByPid(String pid);

}
