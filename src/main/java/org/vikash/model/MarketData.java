package org.vikash.model;


import lombok.Data;

@Data
public class MarketData {

    private String source;
    private String instrument;
    private Side side;
    private double price;
    private long quantity;
    private State state;

    public MarketData(String source, String instrument, Side side, double price, long quantity, State state) {
        this.source = source;
        this.instrument = instrument;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.state = state;
    }
}
