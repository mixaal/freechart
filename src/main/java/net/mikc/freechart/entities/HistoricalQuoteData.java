package net.mikc.freechart.entities;

import java.util.Date;

/**
 * Created by michalconos on 08/10/2016.
 */
public final class HistoricalQuoteData {
    private final Float close, high, low, open, volume;
    private final Date date;

    public HistoricalQuoteData(
            final Date date,
            final Float close,
            final Float high,
            final Float low,
            final Float open,
            final Float volume
    ) {
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
        this.date = date;
    }

    public Float getClose() {
        return close;
    }

    public Float getHigh() {
        return high;
    }

    public Float getLow() {
        return low;
    }

    public Float getOpen() {
        return open;
    }

    public Float getVolume() {
        return volume;
    }

    public Date getDate() {
        return date;
    }
}
