package net.mikc.freechart.quotes;

import net.mikc.freechart.entities.HistoricalQuoteMarketData;
import net.mikc.freechart.exceptions.CantObtainStocksFromServer;
import net.mikc.freechart.entities.HistoricalQuoteData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michalconos on 08/10/2016.
 */
public class HistoricalData {
    //https://www.google.com/finance/getprices?q=ORCL&i=60&p=10d&f=d,c,v,o,h,l&df=cpct&auto=1&ts=1475937959877

    private final Integer period;
    private final String days;
    private final String symbolName;
    private final Client client;
    private interface  FinanceResources {
        String HOST = "https://www.google.com";
        String FINANCE = "finance";
        String GET_PRICES = "getprices";
    }

    public HistoricalData(final String symbolName, final Integer period, final Integer days) {
        this.symbolName = symbolName;
        this.period = period;
        this.days = days.toString() + "d";
        this.client = ClientBuilder.newClient();
    }

    public HistoricalData(final String symbolName) {
        this(symbolName, 60, 10);
    }

    public HistoricalQuoteMarketData get() {
        final long now = new Date().getTime();
        final URI uri = UriBuilder.fromUri(FinanceResources.HOST)
                .path(FinanceResources.FINANCE)
                .path(FinanceResources.GET_PRICES)
                .queryParam("q", symbolName)
                .queryParam("i", period)
                .queryParam("p", days)
                .queryParam("f", "d,c,v,o,h,l")
                .queryParam("df", "cpct")
                .queryParam("auto", "1")
                .queryParam("ts", now)
                .build();
        System.out.println(uri.toString());
        Response response = client.target(uri)
                .request()
                .get();
        if(response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new CantObtainStocksFromServer();
        }
        final String body = response.readEntity(String.class);
        System.out.println(body);
        String []  lines = body.split("\\r\\n|\\n|\\r");
        List<HistoricalQuoteData> marketValues = new ArrayList<>();
        Float minPrice = Float.MAX_VALUE;
        Float maxPrice = Float.MIN_VALUE;
        Date baseLine = null;
        for(final String line: lines) {
            if(line==null || line.isEmpty()) {
                continue;
            }
            if(line.contains(",")) {
                if(line.contains("CLOSE") || line.contains("LOW") || line.contains("HIGH") || line.contains("OPEN") || line.contains("VOLUME")) {
                    continue;
                }

                String marketLine[] = line.split(",");
                if(marketLine.length!=6) {
                    continue;
                }
                Date date;
                if(marketLine[0].startsWith("a")) {
                    final String dateLine = marketLine[0].replace("a", "");
                    date = new Date(1000L*new Long(dateLine));
                    baseLine = date;
                }
                else {
                    long baseLineTime = baseLine.getTime();
                    baseLineTime += period * 1000L  * new Long(marketLine[0]);
                    date = new Date(baseLineTime);
                }
                final HistoricalQuoteData symbolMarketValue =
                        new HistoricalQuoteData(
                                date,
                                new Float(marketLine[1]),
                                new Float(marketLine[2]),
                                new Float(marketLine[3]),
                                new Float(marketLine[4]),
                                new Float(marketLine[5])
                        );
                marketValues.add(symbolMarketValue);
                if(maxPrice<symbolMarketValue.getHigh()) {
                    maxPrice = symbolMarketValue.getHigh();
                }
                if(minPrice>symbolMarketValue.getLow()) {
                    minPrice = symbolMarketValue.getLow();
                }
            }

        }
        return new HistoricalQuoteMarketData(marketValues, minPrice, maxPrice, null, null);
    }
}
