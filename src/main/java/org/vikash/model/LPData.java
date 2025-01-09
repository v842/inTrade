package org.vikash.model;


import lombok.Data;

@Data
public class LPData {
    private Side side;
    private double price;
    private long quantity;

    public LPData(Side side, double price, int quantity) {
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }
}
