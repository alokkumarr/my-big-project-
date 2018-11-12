package com.synchronoss.saw.exception;

import ro.fortsoft.pf4j.PluginException;

@SuppressWarnings("serial")
public class PluginInvokeException extends PluginException{

	private String pluginId;
	
	private String extensionId;

	public PluginInvokeException(String pluginId, Throwable cause) {
		super("Plugin '" + pluginId + "' invoke error.", cause);
		this.pluginId = pluginId;
	}

	public PluginInvokeException(String pluginId, String extensionId, Throwable cause) {
		super("Plugin '" + pluginId + "' extensionId '"+extensionId+"' invoke error.", cause);
		this.pluginId = pluginId;
		this.extensionId = extensionId;
	}
	
	public String getPluginId() {
		return pluginId;
	}

	public String getExtensionId() {
		return extensionId;
	}
	
}
