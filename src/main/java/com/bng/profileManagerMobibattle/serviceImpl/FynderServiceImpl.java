package com.bng.profileManagerMobibattle.serviceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bng.profileManagerMobibattle.dao.FynderDAO;
import com.bng.profileManagerMobibattle.pojo.CheckHERequest;
import com.bng.profileManagerMobibattle.pojo.CheckHEResponse;
import com.bng.profileManagerMobibattle.pojo.ExternalRegisterRequest;
import com.bng.profileManagerMobibattle.pojo.GeneralResponse;
import com.bng.profileManagerMobibattle.pojo.RegisterRequest;
import com.bng.profileManagerMobibattle.pojo.SendOTPRequest;
import com.bng.profileManagerMobibattle.pojo.TempUser;
import com.bng.profileManagerMobibattle.pojo.User;
import com.bng.profileManagerMobibattle.service.FynderService;
import com.bng.profileManagerMobibattle.util.CoreEnums;
import com.bng.profileManagerMobibattle.util.ExternalServiceURIConstants;
import com.bng.profileManagerMobibattle.util.ResponseEnums;
import com.bng.profileManagerMobibattle.util.Utility;
import com.google.gson.JsonObject;

@Service
public class FynderServiceImpl implements FynderService {

	@Autowired
	FynderDAO fynderDAO;

	@Value("${otp.expiry}")
	private Integer expiryTime;

	@Value("${otp.blocktime}")
	private Integer blockTime;

	@Value("${fynder.baseUrl}")
	private String baseUrl;
	
	@Value("${demoNumbers}")
	private String demoNumbers;
	
	private static String numberSeriesAllowed;
	private static List<String> numberSeriesList;
	
	
	private final static Logger logger = LoggerFactory.getLogger(FynderServiceImpl.class);

	@Value("${numberSeriesAllowed}")
	public void setNumberSeriesAllowed(String numberSeriesAllowed) {
		FynderServiceImpl.numberSeriesAllowed = numberSeriesAllowed;
	}

	@PostConstruct
	public  void init() {
		if(numberSeriesAllowed!=null && !numberSeriesAllowed.trim().isEmpty()) {
			numberSeriesList=Arrays.asList(numberSeriesAllowed.trim().split(","));
			numberSeriesList.forEach(e->System.out.println(e));
		}
	}
	
	
	
	
	@Override
	public CheckHEResponse saveHE(String request,Map<String, String> headers) {
		CheckHERequest heRequest;
		CheckHEResponse response=null;
		try {
			heRequest = Utility.gson.fromJson(request, CheckHERequest.class);
		} catch (Exception e) {
			e.printStackTrace();
			response = new CheckHEResponse();
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.INVALIDJSONFAIL.toString(""));
			return response;
		}
		if(heRequest.getDeviceId()==null || heRequest.getDeviceId().trim().equals("")) {
			response = new CheckHEResponse();
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.INVALIDJSONFAIL.toString(""));
			return response;
		}
		String  fynderResponse= Utility.hitGet(baseUrl + ExternalServiceURIConstants.CHECKDEVICE+"?deviceId="+heRequest.getDeviceId(),headers);
		
		if(fynderResponse!=null && !fynderResponse.isEmpty() && fynderResponse.contains("SUCCESS")) {
			
			response = Utility.gson.fromJson(fynderResponse, CheckHEResponse.class);
			return response;
		}
		response = new CheckHEResponse();
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason(ResponseEnums.INVALIDJSONFAIL.toString(""));
		return response;
	}

	@Override
	public GeneralResponse sendOTP(String request,Map<String, String> headers) {
		SendOTPRequest sendOTPRequest;
		GeneralResponse response = new GeneralResponse();
		try {
			sendOTPRequest = Utility.gson.fromJson(request, SendOTPRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.INVALIDJSONFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
			return response;
		}
		if (sendOTPRequest != null && sendOTPRequest.getNumber() != null && !sendOTPRequest.getNumber().isEmpty()) {
			/*if(!checkSeries(sendOTPRequest.getNumber())) {
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.INVALIDOPCO.toString(headers!=null?headers.get("defaultlanguage"):""));
				response.setNoOtp(true);
				return response;
			}*/
			TempUser user = fynderDAO.getTempUser(sendOTPRequest.getNumber());
			if (user == null) { // Temp table does not have user

				logger.debug("User does not exists in temp table");
				user = new TempUser(sendOTPRequest);
				if(user.generateNewOtp(expiryTime)) {
					//sendOtp api
					if(user.sendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
						fynderDAO.saveTempUser(user);
						logger.debug(" Otp Triggered " + user.getNumber());
						response.setStatus(CoreEnums.ResponseSuccess.toString());
						response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
						return response;
					}else {
						logger.error("Can not send Otp " + user.getNumber());
						response.setStatus(CoreEnums.ResponseFailure.toString());
						response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
						return response;
					}
					
				}else {
					//resend otp
					if(user.resendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
						fynderDAO.saveTempUser(user);
						logger.debug(" Otp Triggered " + user.getNumber());
						response.setStatus(CoreEnums.ResponseSuccess.toString());
						response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
						return response;
					}
					else {
						logger.error("Can not send Otp " + user.getNumber());
						response.setStatus(CoreEnums.ResponseFailure.toString());
						response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
						return response;
					}
				}
				
			} else {

				if ((user.getDeviceId().compareTo(sendOTPRequest.getDeviceId())) != 0) { // Registration request
																							// received from another
																							// deviceId for the same
																							// user
					logger.debug(sendOTPRequest.getNumber() + " Request from another Device ID");
					if (user.getStatus().compareTo(CoreEnums.StatusBlocked.toString()) == 0) { // Device is blocked,
																								// check if it can be
																								// unblcoked
						// now.
						long diffSeconds = ((new Date()).getTime() - user.getLastStatusUpdate().getTime()) / 1000;
						if (diffSeconds > blockTime) {
							logger.debug(sendOTPRequest.getNumber()
									+ " Device can be unblocked now, Triggering new Registration");
							user.update(sendOTPRequest);
							if(user.generateNewOtp(expiryTime)) {
								//sendOtp api
								if(user.sendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
									fynderDAO.saveTempUser(user);
									logger.debug(" Otp Triggered " + user.getNumber());
									response.setStatus(CoreEnums.ResponseSuccess.toString());
									response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
								}else {
									logger.error("Can not send Otp " + user.getNumber());
									response.setStatus(CoreEnums.ResponseFailure.toString());
									response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
								}
								
							}else {
								logger.error("Can not send Otp " + user.getNumber());
								response.setStatus(CoreEnums.ResponseFailure.toString());
								response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
								return response;
							}
							
							

						} else {
							logger.error(
									"Can not send Otp as Registration is not allowed with this number and device Id as BLOCKED "
											+ user.getNumber());
							response.setStatus(CoreEnums.ResponseFailure.toString());
							response.setReason(ResponseEnums.NUMBERDEVICEBLOCKED.toString(headers!=null?headers.get("defaultlanguage"):""));
							return response;

						}
					} else if (user.getStatus().compareTo(CoreEnums.StatusOTPGenerated.toString()) == 0) { // Otp is
																											// generated,
																											// check if
																											// it has
						// expired.
						long diffSeconds = ((new Date()).getTime() - user.getLastOtpGenerated().getTime()) / 1000;
						if (diffSeconds > expiryTime) {
							logger.info(
									sendOTPRequest.getNumber() + "OTP expired. Allow registration on another device");
							user.update(sendOTPRequest);
							user.generateNewOtp(expiryTime);
							if(user.sendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
									fynderDAO.saveTempUser(user);
									logger.debug(" Otp Triggered " + user.getNumber());
									response.setStatus(CoreEnums.ResponseSuccess.toString());
									response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
							}else {
									logger.error("Can not send Otp " + user.getNumber());
									response.setStatus(CoreEnums.ResponseFailure.toString());
									response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
							}
								
							

						} else {
							response.setStatus(CoreEnums.ResponseFailure.toString());
							response.setReason(ResponseEnums.REGISTRATIONINPROGRESSFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
							logger.info("Not allowing registration on another device. registration already in progresss"
									+ sendOTPRequest.getNumber());
							return response;

						}
					}

				}

				if (user.getAttempts() > 0 && user.getOtpTriggers() > 0) { // Added on 31st July 2018, Check OTP
																			// triggers as well Wrong attempts
					logger.info("Attempts remaining " + user.getAttempts() + "otp Triggers Remaining");
					user.update(sendOTPRequest);
					if(user.generateNewOtp(expiryTime)) {
						//sendOtp api
						if(user.sendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
							fynderDAO.saveTempUser(user);
							logger.debug(" Otp Triggered " + user.getNumber());
							response.setStatus(CoreEnums.ResponseSuccess.toString());
							response.setReason("OTP has been sent to your registered mobile number");
							return response;
						}else {
							logger.error("Can not send Otp " + user.getNumber());
							response.setStatus(CoreEnums.ResponseFailure.toString());
							response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
							return response;
						}
						
					}else {
						//resend otp
						if(user.resendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
							fynderDAO.saveTempUser(user);
							logger.debug(" Otp Triggered " + user.getNumber());
							response.setStatus(CoreEnums.ResponseSuccess.toString());
							response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
							return response;
						}
						else {
							logger.error("Can not send Otp " + user.getNumber());
							response.setStatus(CoreEnums.ResponseFailure.toString());
							response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
							return response;
						}
					}

				} else {
					long diffSeconds = ((new Date()).getTime() - user.getLastOtpGenerated().getTime()) / 1000;
					if (user.getStatus().compareTo(CoreEnums.StatusOTPGenerated.toString()) == 0 && diffSeconds < blockTime) {
						logger.info(user.getNumber() + " Max Attempts reached for Register Request. Blocking User now");
						user.setStatus(CoreEnums.StatusBlocked.toString());
						fynderDAO.saveTempUser(user);
						response.setStatus(CoreEnums.ResponseFailure.toString());
						response.setReason(ResponseEnums.MAXATTEMPTSREACHED.toString(headers!=null?headers.get("defaultlanguage"):""));
						return response;

					} else if (user.getStatus().compareTo(CoreEnums.StatusBlocked.toString()) == 0
							|| diffSeconds > blockTime) {
						diffSeconds = ((new Date()).getTime() - user.getLastStatusUpdate().getTime()) / 1000;
						if (diffSeconds > blockTime) {
							logger.info("Device Need to unblocked Now");
							user.update(sendOTPRequest);
							user.setAttempts(3);
							user.setOtpTriggers(3);
							user.setStatus(CoreEnums.StatusOTPGenerated.toString()); // Set Status as OtpGenerated
							user.generateNewOtp(expiryTime);
								//sendOtp api
								if(user.sendOtp(demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm")) {
									fynderDAO.saveTempUser(user);
									logger.debug(" Otp Triggered " + user.getNumber());
									response.setStatus(CoreEnums.ResponseSuccess.toString());
									response.setReason(ResponseEnums.OTPSENT.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
								}else {
									logger.error("Can not send Otp " + user.getNumber());
									response.setStatus(CoreEnums.ResponseFailure.toString());
									response.setReason(ResponseEnums.SENDOTPFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
									return response;
								}
						} else {
							logger.info(sendOTPRequest.getNumber() + " Device Is blocked Right now");
							response.setStatus(CoreEnums.ResponseFailure.toString());
							response.setReason(ResponseEnums.DEVICEBLOCKED.toString(headers!=null?headers.get("defaultlanguage"):""));
							return response;

						}
					} else {
						response.setStatus(CoreEnums.ResponseFailure.toString());
						response.setReason("failure");
						return response;
					}
				}

			}
		}
		response.setStatus(CoreEnums.ResponseFailure.toString());
		response.setReason("Number not received");
		return response;
	}

	@Override
	public String register(String request,Map<String, String> headers) {
		RegisterRequest registerRequest = null;
		try {
			registerRequest = Utility.gson.fromJson(request, RegisterRequest.class);
		} catch (Exception e) {
			GeneralResponse response = new GeneralResponse();
			response.setStatus(CoreEnums.ResponseFailure.toString());
			response.setReason(ResponseEnums.INVALIDJSONFAIL.toString(headers!=null?headers.get("defaultlanguage"):""));
			return Utility.gson.toJson(response);
		}
		if (registerRequest.getRegistrationMethod().equalsIgnoreCase(CoreEnums.RegistrationOTP.toString())) {
			TempUser user = fynderDAO.getTempUser(registerRequest.getNumber());
			if (user == null) {
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.USERNOTFOUND.toString(headers!=null?headers.get("defaultlanguage"):""));
				return Utility.gson.toJson(response);
			}
			if (user.getStatus().equals(CoreEnums.StatusBlocked.toString())) {
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.USERISBLOCKED.toString(headers!=null?headers.get("defaultlanguage"):""));
				return Utility.gson.toJson(response);
			} else if (user.getStatus().equals(CoreEnums.StatusOTPGenerated.toString())
					&& ((new Date().getTime() - user.getLastOtpGenerated().getTime()) / 1000 > expiryTime)) {
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.OTPEXPIRED.toString(headers!=null?headers.get("defaultlanguage"):""));
				return Utility.gson.toJson(response);
			} else if (user.getAttempts() <= 0) {
				user.setStatus(CoreEnums.StatusBlocked.toString());
				fynderDAO.saveTempUser(user);
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.MAXATTEMPTSREACHED.toString(headers!=null?headers.get("defaultlanguage"):""));
				return Utility.gson.toJson(response);
			}else if(!user.getDeviceId().equals(registerRequest.getDeviceId())) {
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason(ResponseEnums.INVALIDDEVICEID.toString(headers!=null?headers.get("defaultlanguage"):""));
				return Utility.gson.toJson(response);
			}
			else {
				Boolean isMAtch = user.matchOtp(registerRequest.getOtp(),demoNumbers,headers!=null?headers.get("defaultlanguage"):"mm");
				if (isMAtch) {
					ExternalRegisterRequest externalRequest = new ExternalRegisterRequest();
					externalRequest.setNumber(user.getNumber());
					externalRequest.setDeviceId(user.getDeviceId());
					externalRequest.setRegistrationMethod("OTP");
					externalRequest.setRegistrationMode(CoreEnums.RequestSource.toString());
					externalRequest.setDeviceType(user.getDeviceType());
					externalRequest.setDeviceName(user.getDeviceName());
					externalRequest.setCountryName(user.getCountryName());
					externalRequest.setCountryCallingCode(user.getCountryCallingCode());
					externalRequest.setLanguage(registerRequest.getLanguage());
					String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REGISTERURI,
							Utility.gson.toJson(externalRequest),headers);
					if(response!=null && !response.isEmpty() && response.contains("SUCCESS")) {
						fynderDAO.removeTempUser(user);
						User finalUser = Utility.gson.fromJson(response, User.class);
						finalUser.setDeviceId(user.getDeviceId());
						fynderDAO.saveUser(finalUser);
					}
					return response;

				} else {
					user.setAttempts(user.getAttempts()-1);
					fynderDAO.saveTempUser(user);
					GeneralResponse response = new GeneralResponse();
					response.setStatus(CoreEnums.ResponseFailure.toString());
					response.setReason(ResponseEnums.WRONGOTP.toString(headers!=null?headers.get("defaultlanguage"):""));
					return Utility.gson.toJson(response);

				}
			}
		}
		else if(registerRequest.getRegistrationMethod().equalsIgnoreCase(CoreEnums.RegistrationHe.toString())) {
			/*HERegistration user = fynderDAO.getHE(registerRequest.getNumber());
			if(user==null || user.getDeviceId()==null || !user.getDeviceId().equals(registerRequest.getDeviceId())) {
				GeneralResponse response = new GeneralResponse();
				response.setStatus(CoreEnums.ResponseFailure.toString());
				response.setReason("Invalid deviceId");
				return Utility.gson.toJson(response);
			}*/
			ExternalRegisterRequest externalRequest = new ExternalRegisterRequest();
			externalRequest.setNumber(registerRequest.getNumber());
			externalRequest.setDeviceId(registerRequest.getDeviceId());
			externalRequest.setRegistrationMethod("HE");
			externalRequest.setRegistrationMode(CoreEnums.RequestSource.toString());
			externalRequest.setDeviceType(registerRequest.getDeviceType());
			externalRequest.setDeviceName(registerRequest.getDeviceName());
			externalRequest.setCountryName(registerRequest.getCountryName());
			externalRequest.setLanguage(registerRequest.getLanguage());
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REGISTERURI,
					Utility.gson.toJson(externalRequest),headers);
			
			if(response!=null && !response.isEmpty() && response.contains("SUCCESS")) {
				User finalUser = Utility.gson.fromJson(response, User.class);
				finalUser.setDeviceId(registerRequest.getDeviceId());
				fynderDAO.saveUser(finalUser);
			}
			
			return response;
			
		}
		else {
			String response = Utility.hitPost(baseUrl + ExternalServiceURIConstants.REGISTERURI,
					request,headers);
			if(response!=null && !response.isEmpty() && response.contains("SUCCESS")) {
				User finalUser = Utility.gson.fromJson(response, User.class);
				JsonObject jsonObject = Utility.gson.fromJson(request, JsonObject.class);
				finalUser.setDeviceId(jsonObject.get("deviceId").getAsString());
				logger.info("saving user:-deviceId:"+finalUser.getDeviceId()+",uniqueId:"+finalUser.getUniqueId());
				fynderDAO.saveUser(finalUser);
			}
			return response;
		}
	}

	@Override
	public Boolean validateUser(User user) {
		
		if(user!=null && user.getUniqueId()!=null && !user.getUniqueId().isEmpty() && user.getDeviceId()!=null && !user.getDeviceId().isEmpty()) {
			/*User savedUser = fynderDAO.getUser(user.getUniqueId());
			if(savedUser!=null && savedUser.getDeviceId()!=null)
				return true;*/
			return true;
		}
		return false;
	}

	
	public boolean checkSeries(String number) {
		if(numberSeriesList!=null && !numberSeriesList.isEmpty()) {
			return numberSeriesList.stream().anyMatch(e->number.startsWith(e));
		}else
			return true;
	}


	
	
}
