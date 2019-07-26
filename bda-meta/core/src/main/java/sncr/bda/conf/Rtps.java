package sncr.bda.conf;

public class Rtps {
	
	String topic;
	
	String groupId;
	
	String clientId;
	
	String outputType;
	
	/**
	 * Stream path
	 */
	String maprfsPath;
	
	String model;
	
	String definitionFile;
	
	String confFilePath;
	
	
	

	

	public String getConfFilePath() {
		return confFilePath;
	}

	public void setConfFilePath(String confFilePath) {
		this.confFilePath = confFilePath;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getMaprfsPath() {
		return maprfsPath;
	}

	public void setMaprfsPath(String maprfsPath) {
		this.maprfsPath = maprfsPath;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDefinitionFile() {
		return definitionFile;
	}

	public void setDefinitionFile(String definitionFile) {
		this.definitionFile = definitionFile;
	}

}
