package auctionsniper;

public enum SniperStatus { //SniperState
    JOINING("Joining"),
    BIDDING("Bidding"),
    WINNING("Winning"),
    LOST("Lost"),
    WON("Won");

    private final String statusText;

    SniperStatus(String statusText) {
        this.statusText = statusText;
    }

    public String text() {
        return statusText;
    }
}
