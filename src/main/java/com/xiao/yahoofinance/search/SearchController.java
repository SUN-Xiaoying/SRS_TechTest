package com.xiao.yahoofinance.search;

import com.xiao.yahoofinance.YahooFinance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import yahoofinance.Stock;

import java.io.IOException;

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
}
