package com.bng.profileManagerMobibattle.service;

import java.util.Map;

import com.bng.profileManagerMobibattle.pojo.CheckHEResponse;
import com.bng.profileManagerMobibattle.pojo.GeneralResponse;
import com.bng.profileManagerMobibattle.pojo.User;

public interface FynderService {
	

	public CheckHEResponse saveHE(String request,Map<String, String> headers);
	
	public GeneralResponse sendOTP(String request,Map<String, String> headers);
	
	public String register(String request,Map<String, String> headers);
	
	public Boolean validateUser(User user);
	
	
	
}
