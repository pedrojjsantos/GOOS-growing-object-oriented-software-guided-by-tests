package auctionsniper.e2e;

import auctionsniper.util.FakeAuctionServer;
import org.junit.jupiter.api.*;
import auctionsniper.util.ApplicationRunner;

public class AuctionSniperEndToEndTest {
    private final ApplicationRunner application = new ApplicationRunner();

    @Nested @DisplayName("Single sniper")
    class SingleSniper {
        private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");

        @Test @DisplayName("Should lose if auction closes before bidding")
        void sniper_joins_auction_until_it_closes() throws Exception {
            auction.startSellingItem();
            application.startBiddingIn(auction);
            auction.hasReceivedJoinRequestFromSniper();
            auction.announceClosed();
            application.hasShownSniperHasLostAuction(auction);
        }

        @Test @DisplayName("Should lose if isn't the highest bidder when auction closes")
        void sniper_makes_higher_bid_but_loses() throws Exception {
            auction.startSellingItem();

            application.startBiddingIn(auction);
            auction.hasReceivedJoinRequestFromSniper();

            auction.reportPrice(1000, 98, "other bidder");
            application.hasShownSniperIsBidding(auction, 1000, 1098);

            auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

            auction.announceClosed();
            application.hasShownSniperHasLostAuction(auction);
        }

        @Test @DisplayName("Should win if its the highest bidder when auction closes")
        void sniper_makes_higher_bid_and_wins() throws Exception {
            auction.startSellingItem();

            application.startBiddingIn(auction);
            auction.hasReceivedJoinRequestFromSniper();

            auction.reportPrice(1000, 98, "other bidder");
            application.hasShownSniperIsBidding(auction, 1000, 1098);

            auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

            auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
            application.hasShownSniperIsWinning(auction, 1098);

            auction.announceClosed();
            application.hasShownSniperHasWonAuction(auction, 1098);
        }

        @AfterEach void stopAuction() {
            auction.stop();
        }
    }

    @Nested @DisplayName("Multiple snipers")
    class MultipleSnipers {
        private final FakeAuctionServer auction1 = new FakeAuctionServer("item-54321");
        private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");

        @Test @DisplayName("Sniper bids for multiple items")
        void sniper_bids_for_multiple_items() throws Exception {
            auction1.startSellingItem();
            auction2.startSellingItem();

            application.startBiddingIn(auction1, auction2);
            auction1.hasReceivedJoinRequestFromSniper();
            auction2.hasReceivedJoinRequestFromSniper();

            auction1.reportPrice(1000, 98, "other bidder");
            auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

            auction2.reportPrice(500, 21, "other bidder");
            auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

            auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
            auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

            application.hasShownSniperIsWinning(auction1, 1098);
            application.hasShownSniperIsWinning(auction2, 521);

            auction1.announceClosed();
            auction2.announceClosed();

            application.hasShownSniperHasWonAuction(auction1, 1098);
            application.hasShownSniperHasWonAuction(auction2, 521);
        }

        @AfterEach void stopAuction() {
            auction1.stop();
            auction2.stop();
        }
    }

    @AfterEach void stopApplication() {
        application.stop();
    }
}
