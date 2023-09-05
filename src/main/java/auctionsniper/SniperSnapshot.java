package auctionsniper;

public record SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState status) {
    public SniperSnapshot(String itemId) {
        this(itemId, 0, 0, SniperState.JOINING);
    }

    public static SniperSnapshot joining(String itemID) {
        return new SniperSnapshot(itemID);
    }

    public SniperSnapshot winning(int price) {
        return new SniperSnapshot(itemId, price, price, SniperState.WINNING);
    }

    public SniperSnapshot bidding(int price, int bidAmount) {
        return new SniperSnapshot(itemId, price, bidAmount, SniperState.BIDDING);
    }

    public SniperSnapshot losing(int price) {
        return new SniperSnapshot(itemId, price, lastBid, SniperState.LOSING);
    }

    public SniperSnapshot lost() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.LOST);
    }

    public SniperSnapshot won() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.WON);
    }

    public boolean isWinning() {
        return status == SniperState.WINNING;
    }

    public String statusText() {
        return status.text();
    }

    public boolean hasSameItemAs(SniperSnapshot snapshot) {
        return this.itemId.equals(snapshot.itemId());
    }
}
