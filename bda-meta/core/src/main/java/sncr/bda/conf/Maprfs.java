package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Maprfs {

	@SerializedName("path")
	@Expose
	private String path;
	@SerializedName("outputType")
	@Expose
	private String outputType;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

}
