package com.varian.hook.exception;

import com.sap.me.frame.domain.BusinessException;

public class SerializationMissingException extends BusinessException {
	private static final long serialVersionUID = 1L;
	private String SFC;
	public SerializationMissingException(int errorCode, String traceMessage) {
		super(errorCode, traceMessage);
		this.SFC=traceMessage;
	}
	public String getSFC() {
		return SFC;
	}
	public void setSFC(String format) {
		SFC = format;
	}
}
