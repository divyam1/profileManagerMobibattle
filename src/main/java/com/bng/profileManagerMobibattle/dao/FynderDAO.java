package com.bng.profileManagerMobibattle.dao;

import com.bng.profileManagerMobibattle.pojo.HERegistration;
import com.bng.profileManagerMobibattle.pojo.TempUser;
import com.bng.profileManagerMobibattle.pojo.User;

public interface FynderDAO {
	
	public Boolean saveHE(HERegistration tempUser);
	
	public TempUser getTempUser(String number);
	
	public Boolean saveTempUser(TempUser user);
	
	public Boolean removeTempUser(TempUser user);
	
	public HERegistration getHE(String number);
	
	public Boolean saveUser(User user);
	
	public User getUser(String uniqueId);
}
