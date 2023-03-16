package com.xiao.yahoofinance.webpage;

import com.xiao.yahoofinance.YahooFinance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import yahoofinance.Stock;

import java.io.IOException;

@Controller
public class MainController {
    @GetMapping("/")
    public String showMeTheMoney(Model model) throws IOException {
        Stock stock = YahooFinance.get("INTC");
        model.addAttribute("stock", stock);
        return "stock";
    }
}
