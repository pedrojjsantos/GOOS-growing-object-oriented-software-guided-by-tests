package auctionsniper.ui;

import auctionsniper.Item;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
    void joinAuction(Item item);
}
