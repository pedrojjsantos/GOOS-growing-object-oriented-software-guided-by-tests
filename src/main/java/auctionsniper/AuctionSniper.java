package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener sniperListener;
    private final Auction auction;

    private boolean isWinning = false;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    @Override public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }

    @Override public void currentPrice(int price, int increment, PriceSource source) {
        switch (source) {
            case FromSniper -> notifyWinning();
            case FromOtherBidder -> makeBid(price + increment);
        }
    }

    private void notifyWinning() {
        isWinning = true;
        sniperListener.sniperIsWinning();
    }

    private void makeBid(int amount) {
        auction.bid(amount);
        sniperListener.sniperIsBidding();
    }
}
