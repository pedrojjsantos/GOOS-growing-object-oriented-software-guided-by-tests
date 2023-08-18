package auctionsniper.ui;

import auctionsniper.SniperState;

import java.util.function.Function;

public enum Column {
    ITEM_IDENTIFIER(SniperState::itemId),
    LAST_PRICE(SniperState::lastPrice),
    LAST_BID(SniperState::lastBid),
    SNIPER_STATUS(SniperState::statusText);

    private final Function<SniperState, Object> getValue;

    Column(Function<SniperState, Object> getter) {
        this.getValue = getter;
    }

    public Object valueIn(SniperState state) {
        return getValue.apply(state);
    }

    public static Column at(int offset) {
        return Column.values()[offset];
    }
}
