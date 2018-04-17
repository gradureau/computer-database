package com.excilys.gradureau.computer_database.persistance.dao.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class TimeMapper {

	public static LocalDateTime timestamp2LocalDateTime(Timestamp t) {
		LocalDateTime res = null;
		if(t!=null) {
			LocalDateTime.ofInstant(
					Instant.ofEpochMilli(t.getTime()),
	                TimeZone.getDefault().toZoneId()
	        );
		}
		return res;
	}

}
