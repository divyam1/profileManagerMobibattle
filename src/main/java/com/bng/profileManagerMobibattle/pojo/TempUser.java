package com.bng.profileManagerMobibattle.pojo;

import java.util.Date;
import java.util.Random;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bng.fynder.util.CoreEnums;
import com.bng.fynder.util.Utility;

@Document(collection = "tempuser")
public class TempUser {
	@Id
	private String number;
	private String deviceId;
	private String status; // OtpGenerated, Blocked, WrongOTP
	private Date registrationDate;
	private Date lastStatusUpdate;
	private String countryCallingCode;
	private String countryName;
	private String deviceType;
	private String deviceName;
	private String transactionId;
	private Date lastOtpGenerated;
	private Integer attempts;
	private Integer otpTriggers; // added to block if triggers are more then a value

	public TempUser(SendOTPRequest reg) {
		super();
		this.number = reg.getNumber();
		this.status = CoreEnums.StatusOTPGenerated.toString();
		this.registrationDate = new Date();
		this.lastStatusUpdate = new Date();
		this.countryCallingCode = reg.getCountryCallingCode();
		this.countryName = reg.getCountryName();
		this.deviceType = reg.getDeviceType();
		this.deviceName = reg.getDeviceName();
		this.deviceId = reg.getDeviceId();
		this.attempts = 3; 
		this.otpTriggers = 3; 
		this.lastOtpGenerated = null;
	}

	public TempUser() {
		super();
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.lastStatusUpdate=new Date();
		this.status = status;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastStatusUpdate() {
		return lastStatusUpdate;
	}

	public void setLastStatusUpdate(Date lastStatusUpdate) {
		this.lastStatusUpdate = lastStatusUpdate;
	}

	public String getCountryCallingCode() {
		return countryCallingCode;
	}

	public void setCountryCallingCode(String countryCallingCode) {
		this.countryCallingCode = countryCallingCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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

	

	public Date getLastOtpGenerated() {
		return lastOtpGenerated;
	}

	public void setLastOtpGenerated(Date lastOtpGenerated) {
		this.lastOtpGenerated = lastOtpGenerated;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	public Integer getOtpTriggers() {
		return otpTriggers;
	}

	public void setOtpTriggers(Integer otpTriggers) {
		this.otpTriggers = otpTriggers;
	}

	public boolean generateNewOtp(int timeOut) {
		if (this.lastOtpGenerated == null) {
			System.out.println(" generating new otp ");
			lastStatusUpdate = new Date();
			transactionId=Utility.sdf.format(new Date())+(10+new Random().nextInt(90));
			this.lastOtpGenerated = new Date();
			return true;
		} else {
			long diffSeconds = ((new Date()).getTime() - this.lastOtpGenerated.getTime()) / 1000;
			if (diffSeconds > timeOut) {
				System.out.println("OTP Has Expired. Need a new one");
				lastStatusUpdate = new Date();
				transactionId=Utility.sdf.format(new Date())+(11+new Random().nextInt(80));
				this.lastOtpGenerated = new Date();
				return true;
			} else {
				System.out.println("Using the same OTP");
				return false;
			}
		}
	}

	public Boolean sendOtp(String demoNumbers, String language) {
		return true;
		/*
		System.out.println("demonumbers-"+demoNumbers);
		if(demoNumbers!=null && !demoNumbers.isEmpty()) {
			String numbers[] = demoNumbers.split(",");
			for(String number: numbers) {
				if(number.equalsIgnoreCase(this.number)) {
					this.otpTriggers--;
					return true;
				}
			}
		}
		if(language!=null && !language.isEmpty() && language.equals("en"))
			language="1";
		else
			language="2";
		if(OtpRegistrationService.sendOtp(this.number,this.transactionId,language)) {
			this.otpTriggers--;
			return true;
		}
		return false;*/
	}
	public Boolean resendOtp(String demoNumbers,String language) {
		return true;
		/*
		System.out.println("demonumbers-"+demoNumbers);
		if(demoNumbers!=null && !demoNumbers.isEmpty()) {
			String numbers[] = demoNumbers.split(",");
			for(String number: numbers) {
				if(number.equalsIgnoreCase(this.number)) {
					this.otpTriggers--;
					return true;
				}
			}
		}
		if(language!=null && !language.isEmpty() && language.equals("en"))
			language="1";
		else
			language="2";
		if(OtpRegistrationService.resendOtp(this.number,this.transactionId,language)) {
			this.otpTriggers--;
			return true;
		}
		return false;*/
	}
	
	public void update(SendOTPRequest reg) {
		this.number=reg.getNumber();
		this.countryCallingCode=reg.getCountryCallingCode();
		this.countryName=reg.getCountryName();
		this.deviceType=reg.getDeviceType();
		this.deviceName=reg.getDeviceName();
		this.deviceId=reg.getDeviceId();
	}
	
	public Boolean matchOtp(String otp, String demoNumbers,String language) {
		if(otp==null || otp.isEmpty())
			return false;
		if(otp.equals("1111"))
			return true;
		return false;
		/* if(otp.equals("5219")) {
			 if(demoNumbers!=null && !demoNumbers.isEmpty()) {
					String numbers[] = demoNumbers.split(",");
					for(String number: numbers) {
						if(number.equalsIgnoreCase(this.number)) {
							return true;
						}
					}
				}
		 }
		 if(language!=null && !language.isEmpty() && language.equals("en"))
				language="1";
			else
				language="2";
		return OtpRegistrationService.verfiyOtp(this.number,this.transactionId,otp,language);
		 */
		
	}

}
