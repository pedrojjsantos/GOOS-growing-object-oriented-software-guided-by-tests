package auctionsniper;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AuctionSniperTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);

    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test @DisplayName("Reports loss when auction closes")
    public void reports_loss_when_auction_closes() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test @DisplayName("Bids higher and reports bidding when new price arrives")
    public void bids_higher_and_reports_bidding_when_new_price_arrives() {
        final int price = 1001;
        final int increment = 25;

        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperIsBidding();
        }});

        sniper.currentPrice(price, increment);
    }
}