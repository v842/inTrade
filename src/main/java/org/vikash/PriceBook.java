package org.vikash;

import org.vikash.model.MarketData;

import java.util.TreeSet;

public class PriceBook {

    private TreeSet<MarketData> buys;
    private TreeSet<MarketData> sells;
    private static PriceBook priceBook = null;

    public static PriceBook getInstance(){
        if(priceBook == null) {
            synchronized (PriceBook.class){
                if(priceBook == null) {
                    priceBook = new PriceBook();
                }
            }
        }
        return priceBook;
    }

    private PriceBook() {
        buys = new TreeSet<>((data1, data2) ->
                data1.getPrice() != data2.getPrice() ? (int) (data2.getPrice() - data1.getPrice())
                        : getComparatorValueForEqualPrice(data1, data2));

        sells = new TreeSet<>((data1, data2) ->
                data1.getPrice() != data2.getPrice() ? (int) (data1.getPrice() - data2.getPrice())
                        : getComparatorValueForEqualPrice(data1, data2));
    }

    private static Integer getComparatorValueForEqualPrice(MarketData data1, MarketData data2) {
        if(data1.getQuantity() == data2.getQuantity()){
            return data1.getSource().compareTo(data2.getSource());
        }
        return (int) (data2.getQuantity() - data1.getQuantity());
    }

    public void updateData(MarketData data) {
        switch (data.getSide()) {
            case BUY :
                buys.add(data);
                break;
            case SELL:
                sells.add(data);
                break;
            default:
        }
    }


    public void reset() {
       buys.clear();
       sells.clear();
    }
}
