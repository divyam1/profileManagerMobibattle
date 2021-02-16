package com.bng.profileManagerMobibattle.pojo;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.bng.fynder.util.EventType;

@Document(collection = "event")
public class Event {

	private Date timeStamp;
	private EventType eventType;
	
}
