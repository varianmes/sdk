package com.varian.lsf.bom;

public class TableOperationItem {

	private String operation;
	private String version;
	private String description;
	private boolean currentversion;


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCurrentversion() {
		return currentversion;
	}

	public void setCurrentversion(boolean currentversion) {
		this.currentversion = currentversion;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

}
