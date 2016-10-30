package net.mikc.freechart;

import net.mikc.freechart.algo.AbstractTradingAlgo;
import net.mikc.freechart.algo.AccountBalance;
import net.mikc.freechart.entities.HistoricalQuoteData;
import net.mikc.freechart.entities.HistoricalQuoteMarketData;
import net.mikc.freechart.entities.RealTimeQuoteData;
import net.mikc.freechart.gfx.DrawStocks;
import net.mikc.freechart.quotes.HistoricalData;
import net.mikc.freechart.quotes.RealTimeData;

import java.util.Arrays;

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
        private Marker closeRatio = new Marker("close ratio", Marker.Color.WHITE);
        private Marker openRatio = new Marker("open ratio", Marker.Color.BLUE);
        public DummyTradingAlgo(AccountBalance accountBalance, HistoricalQuoteMarketData historicalQuoteMarketData) {
            super(accountBalance, historicalQuoteMarketData);
        }

        @Override
        public void trade(AccountBalance lowest, AccountBalance highest, HistoricalQuoteData symbolMarketValue) {

            Float price = symbolMarketValue.getClose();
            if(lastPrice==null) {
                lastPrice = price;
            }
            closeRatio.setValue(lastPrice / price);
            openRatio.setValue(2 * symbolMarketValue.getHigh() / symbolMarketValue.getLow());
            symbolMarketValue.setMarkers(Arrays.asList(closeRatio, openRatio));
            if(symbolMarketValue.getClose()>lastPrice) {
                sell(100);
            }
            else {
                buy(100);
            }
            this.lastPrice = price;
        }
    }
}
