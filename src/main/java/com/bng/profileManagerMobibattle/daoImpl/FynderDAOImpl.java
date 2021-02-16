package com.bng.profileManagerMobibattle.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bng.fynder.dao.FynderDAO;
import com.bng.fynder.pojo.HERegistration;
import com.bng.fynder.pojo.TempUser;
import com.bng.fynder.pojo.User;

@Service
public class FynderDAOImpl implements FynderDAO {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public Boolean saveHE(HERegistration tempUser) {
		try {
			mongoTemplate.save(tempUser);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public TempUser getTempUser(String number) {
		Query query = new Query((CriteriaDefinition) Criteria.where("number").is(number));
		try {
			TempUser tempUser = mongoTemplate.findOne(query, TempUser.class);
			return tempUser;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Boolean saveTempUser(TempUser user) {
		try {
			mongoTemplate.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Boolean removeTempUser(TempUser user) {
		try {
			mongoTemplate.remove(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public HERegistration getHE(String number) {
		Query query = new Query((CriteriaDefinition) Criteria.where("number").is(number));
		try {
			HERegistration user = mongoTemplate.findOne(query, HERegistration.class);
			return user;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Boolean saveUser(User user) {
		try {
			mongoTemplate.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public User getUser(String uniqueId) {
		Query query = new Query((CriteriaDefinition) Criteria.where("uniqueId").is(uniqueId));
		try {
			User user = mongoTemplate.findOne(query, User.class);
			return user;
		} catch (Exception e) {
			return null;
		}
	}

}
