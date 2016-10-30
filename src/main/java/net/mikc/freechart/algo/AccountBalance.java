package net.mikc.freechart.algo;

/**
 * Created by michalconos on 08/10/2016.
 */
public final class AccountBalance {
    private Float currentValue; //USD
    private final Float transactionCost; // USD

    public AccountBalance(final Float initialValue, final Float transactionCost) {
        this.currentValue = initialValue;
        this.transactionCost = transactionCost;
    }

    public void transaction(Float amount) {
        currentValue+=amount;
    }

    public Float getCurrentValue() {
        return currentValue;
    }

    public Float getTransactionCost() {
        return transactionCost;
    }
}
