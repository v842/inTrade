package org.vikash.model;

import lombok.Data;

@Data
public class MarketDataValue{
    private double price;
    private long quantity;
    private State state;

    public MarketDataValue(double price, long quantity, State state) {
        this.price = price;
        this.quantity  = quantity;
        this.state  = state;
    }
}
