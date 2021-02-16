package com.bng.profileManagerMobibattle.pojo;

public class DeleteAccountExternalRequest {
	
	private String deviceId;
	private String uniqueId;
	private String requestSource;
	private String language;
	private Boolean isMute;
	private Boolean isVisible;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Boolean isMute() {
		return isMute;
	}
	public void setIsMute(Boolean isMute) {
		this.isMute = isMute;
	}
	public Boolean isVisible() {
		return isVisible;
	}
	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}
}
