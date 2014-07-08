package com.varian.lsf.bom;

public class BomDisplayItem {
	private String bom;
	private String revision;
	private String bomRef;
	private String bomStatus;
	private String isCurrVersion;

	public String getBomStatus() {
		return bomStatus;
	}
	public void setBomStatus(String bomStatus) {
		this.bomStatus = bomStatus;
	}
	public String getIsCurrVersion() {
		return isCurrVersion;
	}
	public void setIsCurrVersion(String isCurrVersion) {
		this.isCurrVersion = isCurrVersion;
	}
	public String getBomRef() {
		return bomRef;
	}
	public void setBomRef(String bomRef) {
		this.bomRef = bomRef;
	}
	public String getBom() {
		return bom;
	}
	public void setBom(String bom) {
		this.bom = bom;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}


}
