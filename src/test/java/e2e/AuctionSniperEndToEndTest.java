package e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @BeforeEach
    void setUp() {
        System.setProperty("com.objogate.wl.keyboard", "US");
    }

    @Test public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.hasShownSniperHasLostAuction();
    }

    @Test
    void sniperMakesHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.hasShownSniperHasLostAuction();
    }

    @AfterEach void stopAuction() {
        auction.stop();
    }
    @AfterEach void stopApplication() {
        application.stop();
    }
}
