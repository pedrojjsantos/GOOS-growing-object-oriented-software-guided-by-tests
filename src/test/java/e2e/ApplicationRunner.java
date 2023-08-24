package e2e;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static auctionsniper.Main.AUCTION_RESOURCE;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + '@' + XMPP_HOSTNAME + '/' + AUCTION_RESOURCE;

    private AuctionSniperDriver driver;

    public void startBiddingIn(FakeAuctionServer... auctions) {
        Thread thread = new Thread(() -> {
            try {
                Main.main(arguments(auctions));
            } catch (Exception e) { e.printStackTrace();}
        }, "Test Application");

        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();

        for (var auction : auctions) {
            driver.showsSniperStatus(auction.getItemId(), 0, 0, SniperState.JOINING.text());
        }
    }

    private static String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;

        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 3] = auctions[i].getItemId();
        }
        return arguments;
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
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SniperState.WINNING.text());;
    }

    public void hasShownSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, SniperState.WON.text());;
    }
}
