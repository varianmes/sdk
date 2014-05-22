package com.varian.lsf.tool;

public class SfcSelectionItem {

	private String sfcNumber;
	private String shopOrder;
	private String material;
	private String operation;
	private String opRevision;	
	private boolean select;
	private String status;
	private String resource;
	private String qty;
	private String opRef;
	private String resRef;
	private String itemRef;
	private String routerRef;
	private String sfcRef;
	private String shopOrderRef;
	private String toolStatus;
	
	public String getToolStatus() {
		return toolStatus;
	}

	public void setToolStatus(String toolStatus) {
		this.toolStatus = toolStatus;
	}

	public String getShopOrderRef() {
		return shopOrderRef;
	}

	public void setShopOrderRef(String shopOrderRef) {
		this.shopOrderRef = shopOrderRef;
	}

	public String getResRef() {
		return resRef;
	}

	public void setResRef(String resRef) {
		this.resRef = resRef;
	}

	public String getItemRef() {
		return itemRef;
	}

	public void setItemRef(String itemRef) {
		this.itemRef = itemRef;
	}

	public String getRouterRef() {
		return routerRef;
	}

	public void setRouterRef(String routerRef) {
		this.routerRef = routerRef;
	}

	public String getSfcRef() {
		return sfcRef;
	}

	public void setSfcRef(String sfcRef) {
		this.sfcRef = sfcRef;
	}

	public String getOpRef() {
		return opRef;
	}

	public void setOpRef(String opRef) {
		this.opRef = opRef;
	}

	public String getOpRevision() {
		return opRevision;
	}

	public void setOpRevision(String opRevision) {
		this.opRevision = opRevision;
	}

	public String getShopOrder() {
		return shopOrder;
	}

	public void setShopOrder(String shopOrder) {
		this.shopOrder = shopOrder;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getSfcNumber() {
		return sfcNumber;
	}

	public void setSfcNumber(String sfcNumber) {
		this.sfcNumber = sfcNumber;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isSelect() {
		return select;
	}
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
}
