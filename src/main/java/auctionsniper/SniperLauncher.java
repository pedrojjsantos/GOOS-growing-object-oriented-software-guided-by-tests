package auctionsniper;

import auctionsniper.ui.UserRequestListener;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector snipers) {
        this.auctionHouse = auctionHouse;
        this.collector = snipers;
    }

    @Override public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(auction, itemId);

        auction.addListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
