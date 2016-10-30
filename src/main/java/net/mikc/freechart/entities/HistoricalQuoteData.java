package net.mikc.freechart.entities;

import net.mikc.freechart.algo.AbstractTradingAlgo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michalconos on 08/10/2016.
 */
public final class HistoricalQuoteData {
    private final Float close, high, low, open, volume;
    private final Date date;
    private List<AbstractTradingAlgo.Marker> markers;

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

    public void setMarkers(List<AbstractTradingAlgo.Marker> markers) {
        List<AbstractTradingAlgo.Marker> markerClones = new ArrayList<>();
        if(markers!=null) {
            for(AbstractTradingAlgo.Marker m: markers) {
                final AbstractTradingAlgo.Marker marker = new AbstractTradingAlgo.Marker(m.getDescription(), m.getColor());
                marker.setValue(m.getValue());
                markerClones.add(marker);
            }
        }
        this.markers = markerClones;
    }

    public List<AbstractTradingAlgo.Marker> getMarkers() {
        return markers;
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
