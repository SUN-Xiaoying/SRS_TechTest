package com.xiao.yahoofinance.search;

import com.xiao.yahoofinance.YahooFinance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@Slf4j
@Controller
public class SearchController {

    @GetMapping("/")
    public String showMeTheSearch(@Param("keyword") String keyword, Model model) throws IOException {
        if (keyword != null) {

            Stock stock = YahooFinance.get(keyword);
            model.addAttribute("stock", stock);
        }
        return "index";
    }

    @GetMapping("/stocks")
    public String showMeTheStock(@Param("keyword") String keyword, Model model) throws IOException {

        List<HistoricalQuote> quotes = YahooFinance.getDailyHistoricalQuotes(keyword);

//        List<Integer> days = new ArrayList<>();
//        List<BigDecimal> prices = new ArrayList<>();
//
//        for (HistoricalQuote quote: quotes) {
//            days.add(quote.getDate().get(Calendar.DAY_OF_MONTH));
//            prices.add(quote.getOpen().stripTrailingZeros());
//            days.add(quote.getDate().get(Calendar.DAY_OF_MONTH));
//            prices.add(quote.getClose().stripTrailingZeros());
//        }

        model.addAttribute(quotes);

        return "search/stock";
    }

}
