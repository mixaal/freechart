package net.mikc.freechart.algo;

import java.util.Date;

/**
 * Created by michalconos on 09/10/2016.
 */
public final class AccountBalanceAtTime {
    private final AccountBalance low;
    private final AccountBalance high;
    private final Date date;

    public AccountBalanceAtTime(final AccountBalance low, final AccountBalance high, final Date date) {
        this.low = low;
        this.high = high;
        this.date = date;
    }

    public AccountBalance getLow() {
        return low;
    }

    public AccountBalance getHigh() {
        return high;
    }

    public Date getDate() {
        return date;
    }
}
