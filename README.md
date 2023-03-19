# Show me the Stock

<table >
<tr>
<td style="background-color:#FFFFE0;">
Here comes a painful Problem,

When running all paramized tests sequentially, all tests passed.
When running the whole folder we got

```bash
SEVERE: MockWebServer[56740] connection from /127.0.0.1 crashed
```

Initially doubt to be version incompatible.
TODO:

-[ ] Upgrade MockWebServer
-[ ] Support Multi-threads

</td>
</tr>
</table>

## Quote

| Terminology |                                                                                    |
|-------------|------------------------------------------------------------------------------------|
| ask         | The lowest price that a seller will accept                                         |
| bid         | The highest price that a buyer is willing to pay for a certain security            |
| price       | Current Price                                                                      |
| bid         | The highest price that a buyer is willing to pay for a certain security            |
| dayLow      | The lowest price at which the security has traded during the current trading day.  |
| dayHigh     | The highest price at which the security has traded during the current trading day. |
| price       | Current Price                                                                      |

<table >
<tr>
<h1>WARNING </h1>
</tr>
<tr style="background-color:darkorange">
<td>

`QUOTES_BASE_URL` and `HISTQUOTES_BASE_URL` has been DECOMMISSIONED

</td>
</tr>
</table>

## URL Smaples

StockQuoteQuery1V7Request:
https://query1.finance.yahoo.com/v7/finance/quote?symbols=AIR.PA

HistQuote2Request:
https://query1.finance.yahoo.com/v7/finance/SHEL?period1=1647601200&period2=1679137200&interval=1d&events=history&crumb=yEVCsOtsKVM

## Bonus

Start YahooApplication and open `localhost:8080`

<img src="/src/main/resources/static/pics/Init.png"/>

<img src="/src/main/resources/static/pics/TSLA.png"/>