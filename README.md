# freechart

Trading algo backtesting application

Creating a simulation on historical data
{code}
        AccountBalance accountBalance = new AccountBalance(10000f, 2f);
        HistoricalData stockData = new HistoricalData("MYCOMPANY").get();
        DummyTradingAlgo tradingAlgo = new DummyTradingAlgo(accountBalance, stockData);
        tradingAlgo.runSimulation();
        DrawStocks stocks = new DrawStocks("MYCOMPANY", stockData, tradingAlgo);
        stocks.pack();
        stocks.setVisible(true);
{code}

Obtaining real-time data
{code}
   final RealTimeData realTimeData = new RealTimeData("MYCOMPANY");
        RealTimeQuoteData quote = realTimeData.get().get(0);
        if(quote.hasData()) {
            System.out.println("quote="+quote.toString());
        }
{code}
