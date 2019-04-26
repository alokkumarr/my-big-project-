package com.synchronoss.saw.batch.plugin.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;

@Service
public class S3ServiceImpl extends SipPluginContract {

	@Override
	public String connectRoute(Long entityId) throws SipNestedRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String connectChannel(Long entityId) throws SipNestedRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String immediateConnectRoute(BisConnectionTestPayload payload)
			throws SipNestedRuntimeException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String immediateConnectChannel(BisConnectionTestPayload payload) throws SipNestedRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDataExists(String data) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

  @Override
  public void executeFileTransfer(String logId, Long jobId, Long channelId, 
      Long routeId, String fileName) {
    // TODO Auto-generated method stub
    
  }
	
	
}
