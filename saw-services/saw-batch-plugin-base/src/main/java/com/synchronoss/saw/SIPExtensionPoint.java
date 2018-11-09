package com.synchronoss.saw;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ro.fortsoft.pf4j.ExtensionPoint;
import ro.fortsoft.pf4j.PluginException;

public interface SIPExtensionPoint extends ExtensionPoint {

	String getToken(Map<String, Object> par,HttpServletRequest req) throws PluginException;
	
	void handleHeaderParams(Map<String, Object> par,HttpServletRequest req) throws PluginException;
	
	void handleRequestParams(Map<String, Object> par,HttpServletRequest req) throws PluginException;
	
	Object handleResult(Object res) throws PluginException;
	
	String getData (String data) throws PluginException;
}
