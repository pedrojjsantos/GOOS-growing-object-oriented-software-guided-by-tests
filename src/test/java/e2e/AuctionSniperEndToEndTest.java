package e2e;

import org.junit.jupiter.api.*;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @BeforeEach
    void setUp() {
        System.setProperty("com.objogate.wl.keyboard", "US");
    }

    @Test @DisplayName("Sniper joins auction until it closes")
    void sniper_joins_auction_until_it_closes() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.hasShownSniperHasLostAuction();
    }

    @Test @DisplayName("Sniper makes higher bid but loses")
    void sniper_makes_higher_bid_but_loses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.hasShownSniperHasLostAuction();
    }

    @Test @DisplayName("Sniper makes higher bid and wins")
    void sniper_makes_higher_bid_and_wins() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(1098);

        auction.announceClosed();
        application.hasShownSniperHasWonAuction(1098);

    }

    @AfterEach void stopAuction() {
        auction.stop();
    }
    @AfterEach void stopApplication() {
        application.stop();
    }
}
