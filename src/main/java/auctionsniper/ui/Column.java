package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

import java.util.function.Function;

public enum Column {
    ITEM_IDENTIFIER("Item", SniperSnapshot::itemId),
    LAST_PRICE("Last Price", SniperSnapshot::lastPrice),
    LAST_BID("Last Bid", SniperSnapshot::lastBid),
    SNIPER_STATUS("State", SniperSnapshot::statusText);

    private final String name;
    private final Function<SniperSnapshot, Object> getValue;

    Column(String name, Function<SniperSnapshot, Object> getter) {
        this.name = name;
        this.getValue = getter;
    }

    public Object valueIn(SniperSnapshot state) {
        return getValue.apply(state);
    }

    public String title() {
        return name;
    }

    public static Column at(int offset) {
        return Column.values()[offset];
    }
}
