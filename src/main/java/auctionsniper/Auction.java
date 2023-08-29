package auctionsniper;

public interface Auction {
    void bid(int amount);

    void join();

    void addListener(AuctionEventListener listener);
}
