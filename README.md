# Show me the Stock

This project is build on [yahoo-finance-api](https://finance.yahoo.com/quotes/API,Documentation/view/v1/), which is a
range of libraries/APIs/methods to obtain historical and real time data for a variety of financial markets and products,
as shown on Yahoo Finance- https://finance.yahoo.com/.

### pros

> Fully reduce code redundancy (`@ParameterizedTest`)

> Test scope perfectly covers API iteration (`QUOTES_QUERY1V7_ENABLED` ?, `HISTQUOTES2_ENABLED` ?, both true and false
> are covered)

> Detail-oriented, (different regions, different intervals)

### cons

- The `integration-test` folder will send requests to yahoo-finance-api web server directly. Even tho I hard-coded the
  start and end time, the returned price may fluctuate within a certain range, which will fail the test.

- `@ParameterizedTest` will fail when running the whole folder, but spot tests always pass.

## Testbed

Test sets are composed of 2 parts.

- test
- integration-test

### test

The **test** has two parts, `mock` and `yahoofinance`.

`yahoofinance` contains unit tests, that will access the server directly and verify the data that will not change in
general (stock name,
symbol, market, currency, etc.)

For `mock`, we will run integration test on `MockWebServer` and the requests will access the local server (localhost)
and match the target CSV file according to `request.yml` mapper.

### integration-test

The **integration-test** still focuses on the two most important requests. Compared to **test**, this time integration
tests will
access the yahoo-finance-api server directly and fetch JSON files.

Of course, the stock prices rises and falls, so I hard-coded the start and end date to ensure the data are consistent.
Still, the sample json files in resources folder determine whether the test passes or fails. (risky)

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

## StockQuote

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