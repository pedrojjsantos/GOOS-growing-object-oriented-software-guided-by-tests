package auctionsniper;

public enum SniperState {
    JOINING("Joining"),
    BIDDING("Bidding"),
    WINNING("Winning"),
    LOST("Lost"),
    WON("Won");

    private final String statusText;

    SniperState(String statusText) {
        this.statusText = statusText;
    }

    public String text() {
        return statusText;
    }
}
