package com.bng.profileManagerMobibattle.util;

public enum CoreEnums {
	
	Default("DEFAULT"),RegistrationOTP("OTP"),StatusBlocked("blocked"),StatusOTPGenerated("otpGenerated"),RegistrationHe("HE"),RegistrationFBKit("FB_KIT"),ResponseSuccess("SUCCESS"),ResponseFailure("FAILURE"),RequestSource("APP");
	
	private String value;

	private CoreEnums(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

}
