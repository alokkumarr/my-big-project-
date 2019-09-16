package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Spark {

	@SerializedName("master")
	@Expose
	private String master;
	@SerializedName("executor.memory")
	@Expose
	private String executorMemory;
	@SerializedName("cores.max ")
	@Expose
	private String coresMax;
	@SerializedName("batch.interval")
	@Expose
	private String batchInterval;
	@SerializedName("app.name")
	@Expose
	private String appName;
	@SerializedName("executor.extraJavaOptions")
	@Expose
	private String executorExtraJavaOptions;
	@SerializedName("driver.extraJavaOptions")
	@Expose
	private String driverExtraJavaOptions;
	@SerializedName("sql.ui.retainedExecutions")
	@Expose
	private String sqlUiRetainedExecutions;
	@SerializedName("streaming.ui.retainedBatches")
	@Expose
	private String streamingUiRetainedBatches;

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getExecutorMemory() {
		return executorMemory;
	}

	public void setExecutorMemory(String executorMemory) {
		this.executorMemory = executorMemory;
	}

	public String getCoresMax() {
		return coresMax;
	}

	public void setCoresMax(String coresMax) {
		this.coresMax = coresMax;
	}

	public String getBatchInterval() {
		return batchInterval;
	}

	public void setBatchInterval(String batchInterval) {
		this.batchInterval = batchInterval;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getExecutorExtraJavaOptions() {
		return executorExtraJavaOptions;
	}

	public void setExecutorExtraJavaOptions(String executorExtraJavaOptions) {
		this.executorExtraJavaOptions = executorExtraJavaOptions;
	}

	public String getDriverExtraJavaOptions() {
		return driverExtraJavaOptions;
	}

	public void setDriverExtraJavaOptions(String driverExtraJavaOptions) {
		this.driverExtraJavaOptions = driverExtraJavaOptions;
	}

	public String getSqlUiRetainedExecutions() {
		return sqlUiRetainedExecutions;
	}

	public void setSqlUiRetainedExecutions(String sqlUiRetainedExecutions) {
		this.sqlUiRetainedExecutions = sqlUiRetainedExecutions;
	}

	public String getStreamingUiRetainedBatches() {
		return streamingUiRetainedBatches;
	}

	public void setStreamingUiRetainedBatches(String streamingUiRetainedBatches) {
		this.streamingUiRetainedBatches = streamingUiRetainedBatches;
	}
}
