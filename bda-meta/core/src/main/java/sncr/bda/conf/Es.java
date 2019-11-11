package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Es {

	@SerializedName("nodes")
	@Expose
	private String nodes;
	@SerializedName("cluster.name")
	@Expose
	private String clusterName;
	@SerializedName("mapping.id")
	@Expose
	private String mappingId;
	@SerializedName("index")
	@Expose
	private String index;
	@SerializedName("net.http.auth.user")
	@Expose
	private String netHttpAuthUser;
	@SerializedName("net.http.auth.pass")
	@Expose
	private String netHttpAuthPass;

	public String getNodes() {
		return nodes;
	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getNetHttpAuthUser() {
		return netHttpAuthUser;
	}

	public void setNetHttpAuthUser(String netHttpAuthUser) {
		this.netHttpAuthUser = netHttpAuthUser;
	}

	public String getNetHttpAuthPass() {
		return netHttpAuthPass;
	}

	public void setNetHttpAuthPass(String netHttpAuthPass) {
		this.netHttpAuthPass = netHttpAuthPass;
	}

}