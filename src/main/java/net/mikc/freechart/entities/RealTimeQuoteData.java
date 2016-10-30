package net.mikc.freechart.entities;

import java.util.Date;

/**
 * Created by michalconos on 11/10/2016.
 */
public final class RealTimeQuoteData {
        private final Float priceCashChange;
        private final Float pricePercentualChange;
        private final Float currentPrice;
        private final Float lastPrice;
        private final String exchange, symbol;
        private final Date timeStamp;

        private static final String NO_SYMBOL = null;

        public RealTimeQuoteData(final String symbol, final Float currentPrice, final Float lastPrice, final Float priceCashChange, final Float pricePercentualChange, final String exchange, final Date timeStamp) {
            this.currentPrice = currentPrice;
            this.priceCashChange = priceCashChange;
            this.pricePercentualChange = pricePercentualChange;
            this.symbol = symbol;
            this.exchange = exchange;
            this.lastPrice = lastPrice;
            this.timeStamp = timeStamp;
        }

        static RealTimeQuoteData noData() {
            return new RealTimeQuoteData(NO_SYMBOL, null, null, null, null, null, null);
        }

        public boolean hasData() {
            return symbol != NO_SYMBOL;
        }

        public Float getPriceCashChange() {
            return priceCashChange;
        }

        public Float getPricePercentualChange() {
            return pricePercentualChange;
        }

        public Float getCurrentPrice() {
            return currentPrice;
        }

        public Float getLastPrice() {
            return lastPrice;
        }

        public String getExchange() {
            return exchange;
        }

        public String getSymbol() {
            return symbol;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        @Override
        public String toString() {
            return "RealTimeQuoteData[symbol="+symbol+", current=$"+currentPrice+", last=$"+lastPrice+", change=$"+priceCashChange+", change="+pricePercentualChange+"p, exchange="+exchange+", time="+timeStamp+"]";
        }
}
