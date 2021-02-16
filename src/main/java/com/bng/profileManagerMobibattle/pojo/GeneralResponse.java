package com.bng.profileManagerMobibattle.pojo;

public class GeneralResponse {
	
	private String status;
	private String reason;
	private Boolean isLogout=false;
	private boolean noOtp;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Boolean getIsLogout() {
		return isLogout;
	}
	public void setIsLogout(Boolean isLogout) {
		this.isLogout = isLogout;
	}
	public boolean getNoOtp() {
		return noOtp;
	}
	public void setNoOtp(boolean noOtp) {
		this.noOtp = noOtp;
	}
	
	
}
