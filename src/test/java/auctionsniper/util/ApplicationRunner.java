package auctionsniper.util;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + '@' + XMPP_HOSTNAME + '/' + AUCTION_RESOURCE;

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();

        for (var auction : auctions) {
            final String itemId = auction.getItemId();

            driver.startBiddingFor(itemId);
            driver.showsSniperStatus(itemId, 0, 0, SniperState.JOINING.text());
        }
    }

    private void startSniper() {
        Thread thread = new Thread(() -> {
            try {
                Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
            } catch (Exception e) { e.printStackTrace();}
        }, "Test Application");

        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperHasLostAuction(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), SniperState.LOST.text());
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SniperState.BIDDING.text());
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SniperState.WINNING.text());
    }

    public void hasShownSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, SniperState.WON.text());
    }
}
