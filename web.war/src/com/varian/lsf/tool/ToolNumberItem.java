package com.varian.lsf.tool;

public class ToolNumberItem {

	private boolean select;
	private String toolNumber;
	private String toolNumDesc;
	private String toolNumStatus;
	private String availableQty;
	private String toolNumRef;
	private String toolGroup;
	
	public String getToolGroup() {
		return toolGroup;
	}

	public void setToolGroup(String toolGroup) {
		this.toolGroup = toolGroup;
	}

	public String getToolNumRef() {
		return toolNumRef;
	}

	public void setToolNumRef(String toolNumRef) {
		this.toolNumRef = toolNumRef;
	}

	public String getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(String availableQty) {
		this.availableQty = availableQty;
	}

	public String getToolNumDesc() {
		return toolNumDesc;
	}

	public void setToolNumDesc(String toolNumDesc) {
		this.toolNumDesc = toolNumDesc;
	}

	public String getToolNumStatus() {
		return toolNumStatus;
	}

	public void setToolNumStatus(String toolNumStatus) {
		this.toolNumStatus = toolNumStatus;
	}

	public String getToolNumber() {
		return toolNumber;
	}

	public void setToolNumber(String toolNumber) {
		this.toolNumber = toolNumber;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isSelect() {
		return select;
	}
}
