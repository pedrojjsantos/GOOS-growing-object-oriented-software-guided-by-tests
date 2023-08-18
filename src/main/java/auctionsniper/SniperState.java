package auctionsniper;

public record SniperState(String itemId, int lastPrice, int lastBid, SniperStatus status) {
    public SniperState(String itemId) {
        this(itemId, 0, 0, SniperStatus.JOINING);
    } //SniperSnapshot
    public static SniperState initialState() {
        return new SniperState("");
    }

    public SniperState winning(int price) {
        return new SniperState(itemId, price, price, SniperStatus.WINNING);
    }

    public SniperState bidding(int price, int bidAmount) {
        return new SniperState(itemId, price, bidAmount, SniperStatus.BIDDING);
    }

    public SniperState lost() {
        return new SniperState(itemId, lastPrice, lastBid, SniperStatus.LOST);
    }

    public SniperState won() {
        return new SniperState(itemId, lastPrice, lastBid, SniperStatus.WON);
    }

    public boolean isWinning() {
        return status == SniperStatus.WINNING;
    }

    public String statusText() {
        return status.text();
    }
}
