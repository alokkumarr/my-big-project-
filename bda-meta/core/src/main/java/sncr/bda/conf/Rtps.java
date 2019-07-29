package sncr.bda.conf;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rtps {

	@SerializedName("fields")
	@Expose
	private Field fields = null;
	@SerializedName("spark")
	@Expose
	private Spark spark;
	@SerializedName("maprfs")
	@Expose
	private Maprfs maprfs;
	@SerializedName("streams")
	@Expose
	private Streams streams;
	@SerializedName("monitoring")
	@Expose
	private Monitoring monitoring;

	@Expose
	private String configFilePath;

	@SerializedName("es")
	@Expose
	private Es es;

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public Field getFields() {
		return fields;
	}

	public void setFields(Field fields) {
		this.fields = fields;
	}

	public Spark getSpark() {
		return spark;
	}

	public void setSpark(Spark spark) {
		this.spark = spark;
	}

	public Maprfs getMaprfs() {
		return maprfs;
	}

	public void setMaprfs(Maprfs maprfs) {
		this.maprfs = maprfs;
	}

	public Streams getStreams() {
		return streams;
	}

	public void setStreams(Streams streams) {
		this.streams = streams;
	}

	public Monitoring getMonitoring() {
		return monitoring;
	}

	public void setMonitoring(Monitoring monitoring) {
		this.monitoring = monitoring;
	}

	public Es getEs() {
		return es;
	}

	public void setEs(Es es) {
		this.es = es;
	}

}
