package com.bng.profileManagerMobibattle.pojo;

public class CheckHEResponse {
	
	private String number="";
	private String status="";
	private String reason="";
	private String registrationMethod="";
	private String fbid="";
	private String googleid="";
	private String mcc="";
	private String mnc="";
	private boolean showAlert;
	private String alert="";
	private String email="";
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
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
	public String getRegistrationMethod() {
		return registrationMethod;
	}
	public void setRegistrationMethod(String registrationMethod) {
		this.registrationMethod = registrationMethod;
	}
	public String getFbid() {
		return fbid;
	}
	public void setFbid(String fbid) {
		this.fbid = fbid;
	}
	
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMnc() {
		return mnc;
	}
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	public boolean isShowAlert() {
		return showAlert;
	}
	public void setShowAlert(boolean showAlert) {
		this.showAlert = showAlert;
	}
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGoogleid() {
		return googleid;
	}
	public void setGoogleid(String googleid) {
		this.googleid = googleid;
	}

}
