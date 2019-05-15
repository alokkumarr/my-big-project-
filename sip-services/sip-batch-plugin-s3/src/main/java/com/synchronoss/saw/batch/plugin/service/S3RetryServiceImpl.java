package com.synchronoss.saw.batch.plugin.service;

import org.springframework.stereotype.Service;

import com.synchronoss.saw.batch.extensions.SipRetryContract;

import javassist.NotFoundException;

@Service
public class S3RetryServiceImpl implements SipRetryContract{

	@Override
	public void retryFailedJob(Long channelId, Long routeId, String channelType, boolean isDisable, String pid,
			String status) throws NotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retryFailedFileTransfer(Long channelId, Long routeId, String fileName, boolean isDisable,
			String source) {
		// TODO Auto-generated method stub
		
	}

}
