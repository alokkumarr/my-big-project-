package com.razor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.razor.raw.generation.rest.properties.RGProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RawGenerationApplication.class)
@WebAppConfiguration
public class RawGenerationApplicationTests {

	@Autowired
	RGProperties rgProperties;
	
	@Autowired
	RemoteTokenServices remoteTokenServices;
	
	@Autowired
	ResourceServerTokenServices tokenServices;
	
	@Test
	public void contextLoads() {
		System.out.println("....................");
	}

	@Test
	public void test2()
	{
		System.out.println(rgProperties.getBrokerUrl());
		System.out.println(rgProperties.getMaxAttachementSize());
		System.out.println(rgProperties.getCheckTokenUrl());
		System.out.println(rgProperties.getClientId());
		System.out.println(rgProperties.getClientSecret());
	}
	
	@Test
	public void testRemoteTokenServices()
	{
		System.out.println(remoteTokenServices);
		//razorRemoteTokenServices.showProperties();
	}
	
	@Test
	public void testTokenServices()
	{
		System.out.println(tokenServices);
	}
	
}
