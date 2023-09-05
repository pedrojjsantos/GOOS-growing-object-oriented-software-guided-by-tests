package auctionsniper;

import auctionsniper.ui.UserRequestListener;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item.id());
        AuctionSniper sniper = new AuctionSniper(auction, item);

        auction.addListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
