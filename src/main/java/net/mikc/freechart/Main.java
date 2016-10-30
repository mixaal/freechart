package net.mikc.freechart;

import net.mikc.freechart.algo.AbstractTradingAlgo;
import net.mikc.freechart.algo.AccountBalance;
import net.mikc.freechart.entities.HistoricalQuoteMarketData;
import net.mikc.freechart.entities.RealTimeQuoteData;
import net.mikc.freechart.gfx.DrawStocks;
import net.mikc.freechart.quotes.HistoricalData;
import net.mikc.freechart.quotes.RealTimeData;

/**
 * Created by michalconos on 30/10/2016.
 */
public class Main {
    public static void main(String [] args) {
        final RealTimeData realTimeData = new RealTimeData("ORCL");
        RealTimeQuoteData quote = realTimeData.get().get(0);
        if(quote.hasData()) {
            System.out.println("quote="+quote.toString());
        }


        AccountBalance accountBalance = new AccountBalance(100_000.0f, 2f);
        HistoricalQuoteMarketData stockData = new HistoricalData("ORCL").get();
        DummyTradingAlgo tradingAlgo = new DummyTradingAlgo(accountBalance, stockData);
        tradingAlgo.runSimulation();
        DrawStocks stocks = new DrawStocks("ORCL", stockData, tradingAlgo);
        stocks.pack();
        stocks.setVisible(true);
    }

    private static class DummyTradingAlgo extends AbstractTradingAlgo {

        private Float lastPrice;
        public DummyTradingAlgo(AccountBalance accountBalance, HistoricalQuoteMarketData historicalQuoteMarketData) {
            super(accountBalance, historicalQuoteMarketData);
        }

        @Override
        public void trade(AccountBalance lowest, AccountBalance highest, Float symbolMarketValue) {
            if(lastPrice==null) {
                lastPrice = symbolMarketValue;
            }
            if(symbolMarketValue>lastPrice) {
                sell(100);
            }
            else {
                buy(100);
            }
            this.lastPrice = symbolMarketValue;
        }
    }
}
