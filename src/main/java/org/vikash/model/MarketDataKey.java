package org.vikash.model;

import lombok.Data;

@Data
public class MarketDataKey{
    private String source;
    private String instrument;
    private Side side;

    public MarketDataKey(String source, String instrument, Side side) {
        this.source = source;
        this.instrument = instrument;
        this.side = side;
    }
}
