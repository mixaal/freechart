package net.mikc.freechart.broker;

/**
 * Created by michalconos on 30/10/2016.
 */
public final class MessageToBroker {
    private final Operation operation;
    private final int amount;

    public MessageToBroker(final Operation operation, final int amount) {
        this.operation = operation;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Operation getOperation() {
        return operation;
    }
}
