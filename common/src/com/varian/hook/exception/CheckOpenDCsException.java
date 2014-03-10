package com.varian.hook.exception;

import com.sap.me.frame.domain.BusinessException;

public class CheckOpenDCsException extends BusinessException {

	private static final long serialVersionUID = 1L;
	private String DC;
	private String SFC;
	private int COUNT;

	public CheckOpenDCsException(String sfcnumber,String dclist,int count) {
		super(20126);
		this.DC = dclist;
		this.SFC = sfcnumber;
		this.COUNT=count;
	}

	public String getDC() {
		return DC;
	}

	public void setDC(String dcList) {
		DC = dcList;
	}
	
	public String getSFC() {
		return SFC;
	}

	public void setSFC(String sfcnumber) {
		SFC = sfcnumber;
	}
	public int getCOUNT() {
		return COUNT;
	}

	public void setCOUNT(int count) {
		COUNT = count;
	}
}
