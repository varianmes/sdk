package com.varian.lsf.bom;

import java.math.BigDecimal;

public class BomComponentItem {
	private String component;
	private String assyoperation;
	private String newoperation;
	private BigDecimal assySequence;
	private String assyQty;
	
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getAssyoperation() {
		return assyoperation;
	}
	public void setAssyoperation(String assyoperation) {
		this.assyoperation = assyoperation;
	}
	public String getNewoperation() {
		return newoperation;
	}
	public void setNewoperation(String newoperation) {
		this.newoperation = newoperation;
	}
	public BigDecimal getAssySequence() {
		return assySequence;
	}
	public void setAssySequence(BigDecimal assySequence) {
		this.assySequence = assySequence;
	}
	public String getAssyQty() {
		return assyQty;
	}
	public void setAssyQty(String assyQty) {
		this.assyQty = assyQty;
	}





}
