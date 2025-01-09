package org.vikash;

import org.vikash.model.LPData;
import org.vikash.model.Message;
import org.vikash.model.Side;

import java.util.List;

public class InTradeSystem {

    private static final MarketDataService marketDataService = MarketDataServiceImpl.getInstance();

    public void update(String source, String instrument, List<LPData> lpDataList) {
        marketDataService.update(new Message(source, instrument, lpDataList));
    }

    public long getTotalQuantityForPriceAndSide(double price, Side side){
        return marketDataService.getTotalQuantityForPriceAndSide(price, side);
    }

    public double getVwapForQuantityAndSide(long quantity, Side side) {
        return marketDataService.getVwapForQuantityAndSide(quantity, side);
    }
    public void reset() {
        marketDataService.reset();
        PriceBook.getInstance().reset();
    }


    public static void main(String[] args) {
        InTradeSystem system = new InTradeSystem();


        system.update("LP1", "USDINR", List.of(
                new LPData(Side.BUY, 82.1500, 1000000),
                new LPData(Side.SELL, 82.1800, 5000000)));


        System.out.println(system.getTotalQuantityForPriceAndSide(82.1500, Side.BUY));
        System.out.println(system.getVwapForQuantityAndSide(5000000, Side.SELL));


        system.update("LP1", "EURINR", List.of(
                new LPData(Side.BUY, 92.1500, 2000000),
                new LPData(Side.SELL, 92.1800, 5000000)));
        System.out.println(system.getTotalQuantityForPriceAndSide(92.1500, Side.BUY));
        System.out.println(system.getVwapForQuantityAndSide(5000000, Side.SELL));



        system.update("LP1", "USDINR", List.of(
                new LPData(Side.BUY, 86.1500, 1000000)));


        System.out.println(system.getTotalQuantityForPriceAndSide(86.1500, Side.BUY));
        System.out.println(system.getVwapForQuantityAndSide(5000000, Side.SELL));

        System.out.println(system.getTotalQuantityForPriceAndSide(82.1500, Side.BUY));
        System.out.println(system.getTotalQuantityForPriceAndSide(82.1800, Side.SELL));


    }
}
