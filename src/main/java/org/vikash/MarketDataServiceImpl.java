package org.vikash;

import lombok.Data;
import org.vikash.model.LPData;
import org.vikash.model.MarketData;
import org.vikash.model.MarketDataKey;
import org.vikash.model.MarketDataValue;
import org.vikash.model.Message;
import org.vikash.model.Side;
import org.vikash.model.State;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class MarketDataServiceImpl implements MarketDataService{

    private Map<MarketDataKey, MarketDataValue> allMarketData= new ConcurrentHashMap<>();
    private Map<Double, Long> buyQntyForPrice = new ConcurrentHashMap<>();
    private Map<Double, Long> sellQntyForPrice = new ConcurrentHashMap<>();
    private Map<Long, Double> buyPriceForQnty = new ConcurrentHashMap<>();
    private Map<Long, Double> sellPriceForQnty = new ConcurrentHashMap<>();
    private AtomicInteger buyCounter=new AtomicInteger(0);
    private AtomicInteger sellCounter=new AtomicInteger(0);

    private MarketDataServiceImpl() {}
    private static MarketDataServiceImpl marketDataService = null;
    public static MarketDataServiceImpl getInstance() {
        if(marketDataService == null) {
            synchronized (MarketDataServiceImpl.class){
                if(marketDataService == null) {
                    marketDataService = new MarketDataServiceImpl();
                }
            }
        }
        return marketDataService;
    }

    @Override
    public void update(Message message) {
        for(Side side: Side.values()) {
            if (!messageContainsSide(message, side)) {
                deleteData(new MarketDataKey(message.getSource(), message.getInstrument(), side));
            }
        }

        message.getLpDataList().forEach(lpData -> {
            MarketData data = getMarketDataObj(message, lpData);
            PriceBook.getInstance().updateData(data);

            MarketDataKey key = new MarketDataKey(message.getSource(), message.getInstrument(), lpData.getSide());
            if(isActive(key)){
                deleteData(key);
            }
            allMarketData.put(key, new MarketDataValue(lpData.getPrice(), lpData.getQuantity(), State.ACTIVE));
            aggregatePrice(lpData.getQuantity(), lpData.getPrice(), lpData.getSide(), 1);
            aggregateQnty(lpData.getQuantity(), lpData.getPrice(), lpData.getSide());
        });
    }

    private boolean isActive(MarketDataKey marketDataKey) {
       return allMarketData.containsKey(marketDataKey)
               && allMarketData.get(marketDataKey).getState().equals(State.ACTIVE);
    }

    private void aggregatePrice(long quantity, double price, Side side, int count) {
        switch (side) {
            case BUY:
                buyPriceForQnty.put(quantity, buyPriceForQnty.getOrDefault(quantity, 0.0) + price);
                buyCounter.getAndAdd(count);
                break;
            case SELL:
                sellPriceForQnty.put(quantity, sellPriceForQnty.getOrDefault(quantity, 0.0) + price);
                sellCounter.getAndAdd(count);
                break;
        }
    }

    private void aggregateQnty(long quantity, double price, Side side) {
        switch (side){
            case BUY:
                buyQntyForPrice.put(price, buyQntyForPrice.getOrDefault(price, 0L) + quantity);
                break;
            case SELL:
                sellQntyForPrice.put(price, sellQntyForPrice.getOrDefault(price, 0L) + quantity);
        }
    }

    private void deleteData(MarketDataKey marketDataKey) {
        allMarketData.get(marketDataKey).setState(State.DELETED);
        MarketDataValue value = allMarketData.get(marketDataKey);
        aggregateQnty(-1 * value.getQuantity(), value.getPrice(), marketDataKey.getSide());
        aggregatePrice(value.getQuantity(), -1.0 * value.getPrice(), marketDataKey.getSide(), -1);
    }

    private static MarketData getMarketDataObj(Message message, LPData lpData) {
        return new MarketData(
                message.getSource(),
                message.getInstrument(),
                lpData.getSide(),
                lpData.getPrice(),
                lpData.getQuantity(),
                State.ACTIVE);
    }

    private boolean messageContainsSide(Message message, Side side) {
        return message.getLpDataList()
                .stream()
                .anyMatch(lpData ->
                        lpData.getSide().equals(side));
    }

    @Override
    public void reset() {
        buyCounter=new AtomicInteger(0);
        sellCounter=new AtomicInteger(0);
        buyPriceForQnty.clear();
        buyQntyForPrice.clear();
        sellPriceForQnty.clear();
        sellQntyForPrice.clear();
        allMarketData.clear();
    }

    @Override
    public long getTotalQuantityForPriceAndSide(double price, Side side) {
        switch (side) {
            case BUY :
                if(!buyQntyForPrice.containsKey(price)) {
                    throw new RuntimeException("price " + price + " is invalid");
                }
                return buyQntyForPrice.get(price);
            case SELL:
                if(!sellQntyForPrice.containsKey(price)) {
                    throw new RuntimeException("price " + price + " is invalid");
                }
                return sellQntyForPrice.get(price);
        }
        throw new RuntimeException("side " + side + " is invalid");
    }

    @Override
    public double getVwapForQuantityAndSide(long qnty, Side side) {
        switch (side) {
            case BUY :
                if(!buyPriceForQnty.containsKey(qnty)) {
                    throw new RuntimeException("qnty " + qnty + " is invalid");
                }
                return buyPriceForQnty.get(qnty) / buyCounter.get();
            case SELL:
                if(!sellPriceForQnty.containsKey(qnty)) {
                    throw new RuntimeException("qnty " + qnty + " is invalid");
                }
                return sellPriceForQnty.get(qnty) / sellCounter.get();
        }
        throw new RuntimeException("side " + side + " is invalid");
    }
}




//interface DeleteData {
//    void delete(String source, String instrument);
//}
//
//class DeleteBuyData implements DeleteData {
//    PriceBook priceBook = PriceBook.getInstance();
//
//    @Override
//    public void delete(String source, String instrument) {
////        priceBook.delete(source, instrument, Side.BUY);
//    }
//}
//
//class DeleteSellData implements DeleteData {
//    PriceBook priceBook = PriceBook.getInstance();
//
//    @Override
//    public void delete(String source, String instrument) {
//
//    }
//}

//class DeleteFactory {
//
//    public static DeleteData getDeleteFactory(Side side) {
//        switch (side) {
//            case BUY :
//                return new DeleteBuyData();
//            case SELL:
//                return new DeleteSellData();
//        }
//        return null;
//    }
//}

