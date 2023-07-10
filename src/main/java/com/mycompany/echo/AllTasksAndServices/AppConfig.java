package com.mycompany.echo.AllTasksAndServices;

import com.microsoft.bot.builder.TurnContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String myStringBean() {
        return "Hello, world!";
    }

        @Bean
        public Long myLongBean() {
            return 12345L;
        }





}
