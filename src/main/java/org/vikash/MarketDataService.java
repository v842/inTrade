package org.vikash;

import org.vikash.model.Message;
import org.vikash.model.Side;

public interface MarketDataService {
    void update(Message message);

    long getTotalQuantityForPriceAndSide(double price, Side side);

    double getVwapForQuantityAndSide(long quantity, Side side);

    void reset();
}
