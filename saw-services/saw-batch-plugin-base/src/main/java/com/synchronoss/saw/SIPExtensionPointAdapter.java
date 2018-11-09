package com.synchronoss.saw;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import ro.fortsoft.pf4j.PluginException;

public class SIPExtensionPointAdapter implements SIPExtensionPoint{

	@Override
	public String getToken(Map<String, Object> par, HttpServletRequest req) throws PluginException{
		return null;
	}

	@Override
	public void handleHeaderParams(Map<String, Object> par, HttpServletRequest req) throws PluginException {
	}

	@Override
	public void handleRequestParams(Map<String, Object> par, HttpServletRequest req) throws PluginException {
	}

	@Override
	public Object handleResult(Object res) throws PluginException {
		return res;
	}

  @Override
  public String getData(String data) throws PluginException {
   
    return data;
  }

}
