package com.mycompany.echo.AllTasksAndServices;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ConvertUTCToIST {
    public String getIST(LocalDateTime localDateTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        localDateTime=localDateTime.plusMinutes(330);
        String istDateTime = localDateTime.format(formatter);

        return istDateTime;
    }
}
