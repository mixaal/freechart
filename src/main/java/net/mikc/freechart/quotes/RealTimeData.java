package net.mikc.freechart.quotes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mikc.freechart.entities.RealTimeQuoteData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by michalconos on 11/10/2016.
 */
public final class RealTimeData {



    //
    // http://finance.google.com/finance/info?client=ig&q=ORCL
    // // [ { "id": "419344" ,"t" : "ORCL" ,"e" : "NYSE" ,"l" : "38.03" ,"l_fix" : "38.03" ,"l_cur" : "38.02" ,"s": "0" ,"ltt":"12:07PM EDT" ,"lt" : "Oct 11, 12:07PM EDT" ,"lt_dts" : "2016-10-11T12:07:35Z" ,"c" : "-0.59" ,"c_fix" : "-0.59" ,"cp" : "-1.54" ,"cp_fix" : "-1.54" ,"ccol" : "chr" ,"pcls_fix" : "38.62" } ]

    private final Client client;
    private final String symbol;

    public RealTimeData(final String symbol) {
        this.symbol = symbol;
        this.client = ClientBuilder.newClient();
    }
    private static final java.lang.reflect.Type internalQuoteDataList = new TypeToken<List<InternalQuoteData>>() {}.getType();
    private static final SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    public List<RealTimeQuoteData> get() {
        final List<RealTimeQuoteData> quotes = new ArrayList<>();
        try {
            final Response r = this.client.target("http://finance.google.com/finance/info").queryParam("q", symbol).request().get();
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                String body = r.readEntity(String.class);
                System.out.println(body);
                body = body.replace("//", "");
                List<InternalQuoteData> internalList = new Gson().fromJson(body, internalQuoteDataList);
                for (InternalQuoteData internal : internalList) {
                    quotes.add(
                            new RealTimeQuoteData(
                                    symbol,
                                    new Float(internal.l_cur),
                                    new Float(internal.l),
                                    new Float(internal.c),
                                    new Float(internal.cp),
                                    internal.e,
                                    ISO8601DATEFORMAT.parse(internal.lt_dts.replace("Z", ""))
                            )
                    );
                }
            }
        }
        catch (ParseException ex) {
            ex.printStackTrace();
        }
        return quotes;

    }





    private static final class InternalQuoteData {
        String id, t, e, l, l_cur, c, cp, lt_dts;
    }

}
