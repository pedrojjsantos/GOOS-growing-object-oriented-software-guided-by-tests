package auctionsniper;

public record Item(String id, int stopPrice) {
    public Item(String id) {
        this(id, Integer.MAX_VALUE);
    }

    public boolean canBid(int amount) {
        return amount <= stopPrice;
    }
}
