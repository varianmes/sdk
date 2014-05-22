package com.varian.lsf.tool;

public class ToolGroupItem {


	private boolean select;
	private String toolGroup;
	private String toolGroupRef;
	private String toolgrpDesc;
	private String toolReqdQty;
	private String attachmentRef;
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAttachmentRef() {
		return attachmentRef;
	}

	public void setAttachmentRef(String attachmentRef) {
		this.attachmentRef = attachmentRef;
	}

	public String getToolReqdQty() {
		return toolReqdQty;
	}

	public void setToolReqdQty(String toolReqdQty) {
		this.toolReqdQty = toolReqdQty;
	}

	public String getToolgrpDesc() {
		return toolgrpDesc;
	}

	public void setToolgrpDesc(String toolgrpDesc) {
		this.toolgrpDesc = toolgrpDesc;
	}

	public String getToolGroupRef() {
		return toolGroupRef;
	}

	public void setToolGroupRef(String toolGroupRef) {
		this.toolGroupRef = toolGroupRef;
	}

	public String getToolGroup() {
		return toolGroup;
	}

	public void setToolGroup(String toolGroup) {
		this.toolGroup = toolGroup;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isSelect() {
		return select;
	}
}
