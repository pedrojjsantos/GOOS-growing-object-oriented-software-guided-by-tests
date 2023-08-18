package e2e;

import auctionsniper.Main;
import auctionsniper.SniperStatus;
import auctionsniper.ui.MainWindow;

import static auctionsniper.Main.AUCTION_RESOURCE;

public class ApplicationRunner {
    public static final String XMTTP_HOSTNAME = "localhost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + '@' + XMTTP_HOSTNAME + '/' + AUCTION_RESOURCE;

    private String itemId;
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        Thread thread = new Thread(() -> {
            try {
                Main.main(XMTTP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
            } catch (Exception e) { e.printStackTrace();}
        }, "Test Application");

        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(SniperStatus.JOINING.text());
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperHasLostAuction() {
        driver.showsSniperStatus(SniperStatus.LOST.text());
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SniperStatus.BIDDING.text());
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, SniperStatus.WINNING.text());;
    }

    public void hasShownSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, SniperStatus.WON.text());;
    }
}
