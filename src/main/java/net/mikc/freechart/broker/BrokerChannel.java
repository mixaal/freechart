package net.mikc.freechart.broker;

import com.google.common.eventbus.EventBus;

/**
 * Created by michalconos on 30/10/2016.
 */
public class BrokerChannel {
    private static final EventBus brokerChannel = new EventBus();

    public static EventBus get() {
        return brokerChannel;
    }
}
