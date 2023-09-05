package auctionsniper.unit;

import auctionsniper.*;
import auctionsniper.AuctionEventListener.PriceSource;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperTest {
    private static final String ITEM_ID = "item-54321";

    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final States sniperState = context.states("sniper");

    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);

    private final AuctionSniper sniper = new AuctionSniper(auction, ITEM_ID);

    @BeforeEach void setUp() {
        this.sniper.addListener(sniperListener);

    }

    @Test @DisplayName("Reports loss when auction closes before bid")
    public void reports_loss_when_auction_closes() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).updateSniperState(with(aSniperThatIs(SniperState.LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test @DisplayName("Reports loss when auction closes while bidding")
    void reports_loss_when_auction_closes_while_bidding() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).updateSniperState(with(aSniperThatIs(SniperState.BIDDING)));
                then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).updateSniperState(with(aSniperThatIs(SniperState.LOST)));
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(135, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test @DisplayName("Bids higher and reports bidding when new price arrives from other bidder")
    public void bids_higher_and_reports_bidding_when_new_price_arrives() {
        final int price = 1001;
        final int increment = 25;
        final int bidAmount = price + increment;

        SniperSnapshot state = new SniperSnapshot(ITEM_ID, price, bidAmount, SniperState.BIDDING);

        context.checking(new Expectations() {{
            oneOf(auction).bid(bidAmount);
            atLeast(1).of(sniperListener).updateSniperState(state);
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test @DisplayName("Reports winning when current price comes from sniper")
    void reports_winning_when_current_price_comes_from_sniper() throws Exception {
        SniperSnapshot endState = new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING);

        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).updateSniperState(with(aSniperThatIs(SniperState.BIDDING)));
                then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).updateSniperState(endState);
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test @DisplayName("Reports won when auction closes while winning")
    void reports_won_when_auction_closes_while_winning() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).updateSniperState(with(aSniperThatIs(SniperState.WINNING)));
                then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).updateSniperState(new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WON));
                when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<>(
                equalTo(state), "sniper that is ", "was")
        {
            @Override protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.status();
            }
        };
    }
}