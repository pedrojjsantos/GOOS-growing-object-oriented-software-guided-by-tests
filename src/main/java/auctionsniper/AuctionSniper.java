package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener sniperListener;
    private final Auction auction;
    private SniperState state;

    private boolean isWinning = false;

    public AuctionSniper(Auction auction, String itemId, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.state = new SniperState(itemId);
    }

    @Override public void auctionClosed() {
        state = state.isWinning() ? state.won() : state.lost();

        sniperListener.updateSniperState(state);
    }

    @Override public void currentPrice(int price, int increment, PriceSource source) {
        int bidAmount = price + increment;

        switch (source) {
            case FromSniper -> state = state.winning(price);
            case FromOtherBidder -> {
                auction.bid(bidAmount);
                state = state.bidding(price, bidAmount);
            }
        }

        sniperListener.updateSniperState(state);
    }
}
