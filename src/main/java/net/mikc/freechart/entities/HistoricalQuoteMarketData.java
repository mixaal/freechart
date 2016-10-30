package net.mikc.freechart.entities;

import java.util.List;

/**
 * Created by michalconos on 08/10/2016.
 */
public final class HistoricalQuoteMarketData {
    private final List<HistoricalQuoteData> symbolMarketValues;
    private final Float minPrice, maxPrice, minVolume, maxVolume;

    public HistoricalQuoteMarketData(
            final List<HistoricalQuoteData> marketValues,
            final Float minPrice,
            final Float maxPrice,
            final Float minVolume,
            final Float maxVolume) {
        this.symbolMarketValues = marketValues;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.minVolume = minVolume;
        this.maxVolume = maxVolume;
    }

    public List<HistoricalQuoteData> getSymbolMarketValues() {
        return symbolMarketValues;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public Float getMinVolume() {
        return minVolume;
    }

    public Float getMaxVolume() {
        return maxVolume;
    }
}
