package com.bng.profileManagerMobibattle.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bng.profileManagerMobibattle.util.Utility;

@Service
public class OtpRegistrationService {


	private static String sendOtpUrl;

	private static String verfiyOtpUrl;

	private static String resendOtpUrl;
	
	
	
	@Value("${sendOtpUrl}")
	public  void setSendOtpUrl(String sendOtpUrl) {
		OtpRegistrationService.sendOtpUrl = sendOtpUrl;
	}
	
	@Value("${resendOtpUrl}")
	public  void setReSendOtpUrl(String resendOtpUrl) {
		OtpRegistrationService.resendOtpUrl = resendOtpUrl;
	}

	@Value("${verfiyOtpUrl}")
	public  void setVerfiyOtpUrl(String verfiyOtpUrl) {
		OtpRegistrationService.verfiyOtpUrl = verfiyOtpUrl;
	}
	
	


//	public  static boolean sendOtp(String number,String transactionId,String language) {
//		SendOtpResponse otpResponse=null;
//		try {
//			String url=sendOtpUrl;
//			url=url.replace("$msisdn$",number);
//			url=url.replace("$transactionId$",transactionId);
//			url=url.replace("$language$",language);
//			String response=Utility.hitGet(url,null);
//			if(response !=null && !response.trim().isEmpty()) {
//				otpResponse=parseResponse(response);
//				if(otpResponse!=null && otpResponse.getError_code()!=null && otpResponse.getError_code().equals("0"))
//					return true;
//			}
//		}catch(Exception e) {
//
//			return false;
//		}
//
//		return false;
//	}

//	public  static boolean resendOtp(String number,String transactionId,String language) {
//		SendOtpResponse otpResponse=null;
//		System.out.println("Hitting resend OTP request...");
//		try {
//			String url=resendOtpUrl;
//			url=url.replace("$msisdn$",number);
//			url=url.replace("$transactionId$",transactionId);
//			url=url.replace("$language$",language);
//			System.out.print("verify otp: "+url);
//			System.out.println("Hitting resend OTP request..."+url);
//			String response=Utility.hitGet(url,null);
//			if(response !=null && !response.trim().isEmpty()) {
//				otpResponse=parseResponse(response);
//				if(otpResponse!=null && otpResponse.getError_code()!=null && otpResponse.getError_code().equals("0"))
//					return true;
//			}
//		}catch(Exception e) {
//			System.out.println("Verify OTP failed");
//			e.printStackTrace();
//			return false;
//		}
//
//		return false;
//	}


//	public static boolean verfiyOtp(String number,String transactionId,String otp,String language) {
//		SendOtpResponse otpResponse=null;
//		System.out.println("Hitting verify OTP request...");
//		try {
//			String url=verfiyOtpUrl;
//			url=url.replace("$msisdn$",number);
//			url=url.replace("$transactionId$",transactionId);
//			url=url.replace("$otp$", otp);
//			url=url.replace("$language$",language);
//			System.out.println("Hitting verify OTP request..."+url);
//			String response=Utility.hitGet(url,null);
//			if(response !=null && !response.trim().isEmpty()) {
//				otpResponse=parseResponse(response);
//				if(otpResponse!=null && otpResponse.getError_code()!=null && otpResponse.getError_code().equals("0"))
//					return true;
//			}
//			
//		}catch(Exception e) {
//			System.out.println("Verify OTP failed");
//			e.printStackTrace();
//			return false;
//		}
//		return false;
//	}
//	
//	
//	
//	public static SendOtpResponse parseResponse(String response) {
//		SendOtpResponse obj=null;
//		try {
//			
//            JAXBContext jaxbContext = JAXBContext.newInstance(SendOtpResponse.class);
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            InputStream is = new ByteArrayInputStream(response.getBytes());
//            obj = (SendOtpResponse) jaxbUnmarshaller.unmarshal(is);
//		}catch(Exception e){
//			e.printStackTrace();
//			return obj;
//		}
//		return obj;
//	}



}
