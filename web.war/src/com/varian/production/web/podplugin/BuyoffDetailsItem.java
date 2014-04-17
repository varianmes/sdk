package com.varian.production.web.podplugin;

public class BuyoffDetailsItem {

	private String sfc;
	private String buyoff;
	private String description;
	private String action;
	private String comments;
	private String pastUser;
	private String buyoffState;
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPastUser() {
		return pastUser;
	}

	public void setPastUser(String pastUser) {
		this.pastUser = pastUser;
	}

	public String getBuyoffState() {
		return buyoffState;
	}

	public void setBuyoffState(String buyoffState) {
		this.buyoffState = buyoffState;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setSfc(String sfc) {
		this.sfc = sfc;
	}

	public String getSfc() {
		return sfc;
	}

	public String getBuyoff() {
		return buyoff;
	}

	public void setBuyoff(String buyoff) {
		this.buyoff = buyoff;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}
