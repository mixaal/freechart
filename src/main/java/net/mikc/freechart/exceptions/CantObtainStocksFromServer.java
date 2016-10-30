package net.mikc.freechart.exceptions;

/**
 * Created by michalconos on 08/10/2016.
 */
public final class CantObtainStocksFromServer extends FreeChartException {
    public CantObtainStocksFromServer() {
        super("Can't obtain stocks from the server");
    }
}
