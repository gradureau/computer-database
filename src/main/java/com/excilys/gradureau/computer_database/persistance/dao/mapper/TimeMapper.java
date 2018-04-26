package com.excilys.gradureau.computer_database.persistance.dao.mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimeMapper {

    public static LocalDateTime timestamp2LocalDateTime(Timestamp t) {
        LocalDateTime res = null;
        if (t != null) {
            res = t.toLocalDateTime();
        }
        return res;
    }

}
