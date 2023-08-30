package it;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuctionHouse;
import e2e.ApplicationRunner;
import e2e.FakeAuctionServer;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class XMPPAuctionTest {
    private FakeAuctionServer server;
    private XMPPAuctionHouse auctionhouse;

    @BeforeEach void setUp() throws XMPPException {
        this.server = new FakeAuctionServer("item-54321");
        server.startSellingItem();

        this.auctionhouse = XMPPAuctionHouse.connect(
                ApplicationRunner.XMPP_HOSTNAME,
                ApplicationRunner.SNIPER_ID,
                ApplicationRunner.SNIPER_PASSWORD
        );
    }
    @AfterEach void stopAuction() {
        server.stop();
    }
    @AfterEach void disconnect() {
        auctionhouse.disconnect();
    }

    @Test @DisplayName("Receives events from auction server after joining")
    void receives_events_from_auction_server_after_joining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionhouse.auctionFor(server.getItemId());
        auction.addListener(auctionClosedListener(auctionWasClosed));
        auction.join();

        server.hasReceivedJoinRequestFromSniper();
        server.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS), "should have been closed");
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }
            public void currentPrice(int price, int increment, PriceSource priceSource) {}
        };
    }
}