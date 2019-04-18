package com.synchronoss.saw.batch.ingestion.sftp.connection;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.synchronoss.saw.batch.connections.SIPConnectionsService;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;

@Service
public class SFTPConnectionService implements 
     SIPConnectionsService {

  @Autowired
  @Qualifier("sftpService")
  private SipPluginContract sftpServiceImpl;
  
  @Override
  public String connectRoute(Long routeId) {
    return JSON.toJSONString(sftpServiceImpl.connectRoute(routeId));
  }

  @Override
  public String connectImmediateRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    return JSON.toJSONString(sftpServiceImpl.immediateConnectRoute((payload)));
  }

  @Override
  public String connectChannel(Long channelId) {
    return JSON.toJSONString(sftpServiceImpl.connectChannel(channelId));
  }

  @Override
  public String connectImmediateChannel(BisConnectionTestPayload payload) {
    return JSON.toJSONString(sftpServiceImpl.immediateConnectChannel(payload));
  }


  @Override
  public String connectChannelWithoutSave(BisConnectionTestPayload payload) 
      throws SipNestedRuntimeException, IOException   {
    return JSON.toJSONString(sftpServiceImpl.immediateConnectChannel(payload));
  }

  @Override
  public String connectRouteWithoutSave(BisConnectionTestPayload payload)
		  throws SipNestedRuntimeException, IOException{
	  return JSON.toJSONString(sftpServiceImpl.immediateConnectRoute((payload)));
  }

  
  
  
}
