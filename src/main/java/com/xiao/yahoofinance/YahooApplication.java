package com.xiao.yahoofinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class YahooApplication {

    public static void main(String[] args) {

        SpringApplication.run(YahooApplication.class, args);
    }

}
