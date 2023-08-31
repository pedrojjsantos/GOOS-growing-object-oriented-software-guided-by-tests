package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(Auction auction, String itemId) {
        this.auction = auction;
        this.snapshot = new SniperSnapshot(itemId);
    }

    @Override public void auctionClosed() {
        snapshot = snapshot.isWinning() ? snapshot.won() : snapshot.lost();

        listeners.announce().updateSniperState(snapshot);
    }

    @Override public void currentPrice(int price, int increment, PriceSource source) {
        int bidAmount = price + increment;

        switch (source) {
            case FromSniper -> snapshot = snapshot.winning(price);
            case FromOtherBidder -> {
                auction.bid(bidAmount);
                snapshot = snapshot.bidding(price, bidAmount);
            }
        }

        listeners.announce().updateSniperState(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return this.snapshot;
    }

    public void addListener(SniperListener sniperListener) {
        listeners.addListener(sniperListener);
    }
}
