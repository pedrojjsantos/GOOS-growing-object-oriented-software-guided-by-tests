package auctionsniper;

public interface AuctionEventListener {
    void auctionClosed();

    void currentPrice(int currentPrice, int increment);
}
