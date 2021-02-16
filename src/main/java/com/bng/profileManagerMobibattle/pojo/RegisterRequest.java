package com.bng.profileManagerMobibattle.pojo;

public class RegisterRequest {
	
	private String number;
	private String deviceId;
	private String otp;
	private String registrationMethod;
	private String deviceType;
	private String deviceName;
	private String countryName;
	private String language;
	private String email;
	private String fbid;
	private String googleid;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getRegistrationMethod() {
		return registrationMethod;
	}
	public void setRegistrationMethod(String registrationMethod) {
		this.registrationMethod = registrationMethod;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFbid() {
		return fbid;
	}
	public void setFbid(String fbid) {
		this.fbid = fbid;
	}
	
	public String getGoogleid() {
		return googleid;
	}
	public void setGoogleid(String googleid) {
		this.googleid = googleid;
	}
}
