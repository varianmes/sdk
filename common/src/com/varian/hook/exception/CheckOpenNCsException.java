package com.varian.hook.exception;

import com.sap.me.frame.domain.BusinessException;

public class CheckOpenNCsException extends BusinessException{
	private static final long serialVersionUID = 1L;
	private String sfc;private String operation;

	public CheckOpenNCsException(String sfc,String operation) {
		super(20223);
		this.sfc = sfc;
		this.operation = operation;
		
	}

	public String getSfc() {
		return sfc;
	}


	public String getOperation() {
		return operation;
	}
}
