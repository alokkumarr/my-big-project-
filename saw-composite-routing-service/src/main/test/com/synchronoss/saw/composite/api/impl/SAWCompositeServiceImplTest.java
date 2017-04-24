package com.synchronoss.saw.composite.api.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synchronoss.saw.composite.SAWCompositeProperties;
import com.synchronoss.saw.composite.ServiceUtils;
import com.synchronoss.saw.composite.model.Contents;
import com.synchronoss.saw.composite.model.Contents.Action;
import com.synchronoss.saw.composite.model.Key;
import com.synchronoss.saw.composite.model.Links;
import com.synchronoss.saw.composite.model.RoutingPayload;

/**
 * Test for ({@link SAWCompositeServiceImpl})
 * 
 * @author saurav.paul
 *
 */
@RunWith(SpringRunner.class)
@RestClientTest({ SAWCompositeServiceImpl.class, SAWCompositeProperties.class, ServiceUtils.class })
@TestPropertySource(properties = { "metadata.url : http://client.sncrbda.dev.cloud.synchronoss.net",
		"metadata.url.context=/md" })
public class SAWCompositeServiceImplTest {

	@Autowired
	private SAWCompositeServiceImpl sawCompositeServiceImpl;
	
	@Test
	public void menuItemsTest() throws JsonProcessingException 
	{
		RoutingPayload routingPayload = new RoutingPayload();
		Contents contents = new Contents();
		contents.setAction(Action.SEARCH);
		List<Key>  keysObject = new ArrayList<Key>();
		Key keys = new Key();
		keys.setAdditionalProperty("type", "menu");
		keys.setAdditionalProperty("module", "analyze");
		keys.setAdditionalProperty("customerCode", "ATT");
		keysObject.add(keys);
		contents.setKeys(keysObject);
		routingPayload.setContents(contents);
		List<Object> data = new ArrayList<Object>();
		data.add("");
		routingPayload.setData(data);
		Links links = new Links();
		routingPayload.setLinks(links);
		routingPayload = sawCompositeServiceImpl.menuItems(routingPayload);
		assertThat(routingPayload.getContents().getAnalyze()).isNotNull();

	}
	
/*	@Test
	public void newAnalysisTest() throws JsonProcessingException 
	{
		RoutingPayload routingPayload = new RoutingPayload();
		Contents contents = new Contents();
		contents.setAction(Action.READ);
		Key keys = new Key();
		List<Key>  keysObject = new ArrayList<Key>();
		keys.setAdditionalProperty("module", "ANALYZE");
		keys.setAdditionalProperty("customerCode", "ATT");
		keys.setAdditionalProperty("type", "semantic");
		keys.setAdditionalProperty("dataSecurityKey", "20170612ATT");
		keysObject.add(keys);
		contents.setKeys(keysObject);
		routingPayload.setContents(contents);
		List<Object> data = new ArrayList<Object>();
		data.add("");
		routingPayload.setData(data);
		Links links = new Links();
		routingPayload.setLinks(links);
		routingPayload = sawCompositeServiceImpl.newAnalysis(routingPayload);
		assertThat(routingPayload.getContents().getAnalyze()).isNotNull();
		
	}
*/
}
