package e2e;

public class FakeAuctionServer {
    private final String itemId;
    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
    }

    public void startSellingItem() {
        throw new UnsupportedOperationException("TODO");
    }

    public void hasReceivedJoinRequestFromSniper() {
        throw new UnsupportedOperationException("TODO");
    }

    public void announceClosed() {
        throw new UnsupportedOperationException("TODO");
    }

    public void stop() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getItemId() {
        return itemId;
    }
}
