package com.mycompany.echo.AllTasksAndServices;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ConvertUTCToIST {
    /**
     * this is user to convert utc to ist
     * @param localDateTime this is local data time
     * @return return time string
     */
    public String getIST(LocalDateTime localDateTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss");

//        localDateTime=localDateTime.plusMinutes(330);
        String istDateTime = localDateTime.format(formatter);

        return istDateTime;
    }
}
