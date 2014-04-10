package com.varian.hook.exception;

import com.sap.me.frame.domain.BusinessException;

public class DCInvalidforSFCException extends BusinessException {

	private static final long serialVersionUID = 1L;
	private String SFC;

	public DCInvalidforSFCException(String sfcnumber,int count) {
		super(20129);
		this.SFC = sfcnumber;
	}

	public String getSFC() {
		return SFC;
	}

	public void setSFC(String sfcnumber) {
		SFC = sfcnumber;
	}
}
