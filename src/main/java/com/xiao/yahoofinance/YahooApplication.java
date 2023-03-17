package com.xiao.yahoofinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class YahooApplication {

    public static void main(String[] args) {


        //		BigDecimal price = stock.getQuote().getPrice();
        //		BigDecimal change = stock.getQuote().getChangeInPercent();
        //		BigDecimal peg = stock.getStats().getPeg();
        //		BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
        //
        //		stock.print();
        SpringApplication.run(YahooApplication.class, args);
    }

}
