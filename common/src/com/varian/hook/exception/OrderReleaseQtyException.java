package com.varian.hook.exception;

import com.sap.me.frame.domain.BusinessException;

public class OrderReleaseQtyException extends BusinessException {
	private static final long serialVersionUID = 1L;
	private String QTY;
	public OrderReleaseQtyException(int errorCode, String traceMessage) {
		super(errorCode, traceMessage);
		this.QTY=traceMessage;
	}
	public String getQTY() {
		return QTY;
	}
	public void setQTY(String format) {
		QTY = format;
	}
}