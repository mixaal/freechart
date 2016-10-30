package net.mikc.freechart.algo;

import com.google.common.eventbus.EventBus;
import com.sun.corba.se.pept.broker.Broker;
import net.mikc.freechart.broker.BrokerChannel;
import net.mikc.freechart.broker.MessageToBroker;
import net.mikc.freechart.broker.Operation;
import net.mikc.freechart.entities.HistoricalQuoteMarketData;
import net.mikc.freechart.entities.HistoricalQuoteData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michalconos on 08/10/2016.
 */
public abstract class AbstractTradingAlgo {

    private final AccountBalance lowest, highest;
    private final HistoricalQuoteMarketData marketData;
    private final List<AccountBalanceAtTime> accountBalanceSnapshots;
    private  int symbolAmount;

    private MessageToBroker messageToBroker;

    public AbstractTradingAlgo(final AccountBalance accountBalance, final HistoricalQuoteMarketData marketData) {
        this.lowest = new AccountBalance(accountBalance.getCurrentValue(), accountBalance.getTransactionCost());
        this.highest = new AccountBalance(accountBalance.getCurrentValue(), accountBalance.getTransactionCost());
        this.marketData = marketData;
        this.accountBalanceSnapshots = new ArrayList<>();
        this.symbolAmount = 0;
    }

    public abstract void trade(AccountBalance lowest, AccountBalance highest, Float symbolMarketValue);

    protected Float getPrice(HistoricalQuoteData historicalQuoteData) {
        return 0.5f * (historicalQuoteData.getLow() + historicalQuoteData.getHigh());
    }



    protected void buy(int amount) {
        messageToBroker = new MessageToBroker(Operation.BUY, amount);
    }

    protected void sell(int amount) {
        messageToBroker = new MessageToBroker(Operation.SELL, amount);
    }

    public void runSimulation() {
        for(final HistoricalQuoteData marketValue: marketData.getSymbolMarketValues()) {
            messageToBroker = null;
            final AccountBalanceAtTime accountBalanceAtTime = new AccountBalanceAtTime(
                    new AccountBalance(lowest.getCurrentValue(), lowest.getTransactionCost()),
                    new AccountBalance(highest.getCurrentValue(), highest.getTransactionCost()),
                    marketValue.getDate()
            );
            accountBalanceSnapshots.add(accountBalanceAtTime);
            trade(lowest, highest, getPrice(marketValue));
            processMessageToBroker(lowest, highest, marketValue);
        }
    }

    private void processMessageToBroker(final AccountBalance lowestAccountBalance, final AccountBalance highestAccuntBalance, HistoricalQuoteData historicalQuoteData) {
        if(messageToBroker!=null) {
            switch (messageToBroker.getOperation()) {
                case BUY:
                    buy(messageToBroker.getAmount(), lowestAccountBalance, highestAccuntBalance, historicalQuoteData);
                    break;
                case SELL:
                    sell(messageToBroker.getAmount(), lowestAccountBalance, highestAccuntBalance, historicalQuoteData);
                    break;
            }
        }
    }

    private void buy(int amount, final AccountBalance lowestAccountBalance, final AccountBalance highestAccountBalance, final HistoricalQuoteData symbol) {
        float priceLow = lowestAccountBalance.getTransactionCost() + amount * symbol.getLow();
        float priceHigh = highestAccountBalance.getTransactionCost() + amount * symbol.getHigh();
        //System.out.println("balance="+accountBalance.getCurrentValue()+"  price="+price);


        if(highestAccountBalance.getCurrentValue() > priceHigh && lowestAccountBalance.getCurrentValue() > priceLow) {
            highestAccountBalance.transaction(-priceLow);
            lowestAccountBalance.transaction(-priceHigh);
            symbolAmount += amount;
        }
    }

    private void sell(int amount, final AccountBalance lowestAccountBalance, final AccountBalance highestAccountBalance, final HistoricalQuoteData symbol) {
        float priceLow = - lowestAccountBalance.getTransactionCost() + amount * symbol.getLow();
        float priceHigh = - highestAccountBalance.getTransactionCost() + amount * symbol.getHigh();
        if(symbolAmount>=amount) {
            lowestAccountBalance.transaction(priceLow);
            highestAccountBalance.transaction(priceHigh);
            symbolAmount-=amount;
        }
    }

    public List<AccountBalanceAtTime> getAccountBalanceSnapshots() {
        return accountBalanceSnapshots;
    }
}
