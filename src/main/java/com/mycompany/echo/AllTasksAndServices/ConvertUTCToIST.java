package com.mycompany.echo.AllTasksAndServices;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.time.ZoneId;

import java.time.format.DateTimeFormatter;

@Component
public class ConvertUTCToIST {
    public String getIST(LocalDateTime localDateTime){


        // Convert to Indian Standard Time (IST)
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istDateTime = localDateTime.atZone(istZone).toLocalDateTime();

        // Format the IST datetime as a string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return istDateTime.format(formatter);

    }
}
