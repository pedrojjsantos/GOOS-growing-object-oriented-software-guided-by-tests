package auctionsniper;

import auctionsniper.AuctionEventListener.PriceSource;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final States sniperState = context.states("sniper");

    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);

    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test @DisplayName("Reports loss when auction closes before bid")
    public void reports_loss_when_auction_closes() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test @DisplayName("Reports loss when auction closes while bidding")
    void reports_loss_when_auction_closes_while_bidding() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperIsBidding();
                then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperLost();
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test @DisplayName("Bids higher and reports bidding when new price arrives from other bidder")
    public void bids_higher_and_reports_bidding_when_new_price_arrives() {
        final int price = 1001;
        final int increment = 25;

        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperIsBidding();
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test @DisplayName("Reports winning when current price comes from sniper")
    void reports_winning_when_current_price_comes_from_sniper() throws Exception {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperIsWinning();
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }

    @Test @DisplayName("Reports won when auction closes while winning")
    void reports_won_when_auction_closes_while_winning() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperIsWinning();
                then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperWon();
                when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
}