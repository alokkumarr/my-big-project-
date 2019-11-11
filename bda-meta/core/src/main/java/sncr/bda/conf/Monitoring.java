package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Monitoring {

	@SerializedName("controlfile.path")
	@Expose
	private String controlfilePath;
	@SerializedName("interval")
	@Expose
	private String interval;

	public String getControlfilePath() {
		return controlfilePath;
	}

	public void setControlfilePath(String controlfilePath) {
		this.controlfilePath = controlfilePath;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

}
