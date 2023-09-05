package auctionsniper.unit;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.Matchers.equalTo;

public class SniperLauncherTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final States auctionState =
            context.states("auction state").startsAs("not joined");

    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);

    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test @DisplayName("Adds a new sniper to collector and joins auction")
    void adds_a_new_sniper_to_collector_and_joins_auction() {
        final String itemId = "item 123";

        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId);
                will(returnValue(auction));

            oneOf(auction).addListener(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));

            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));

            oneOf(auction).join();
                then(auctionState.is("joined"));
        }});

        launcher.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<>(equalTo(itemId), "sniper with item id", "item") {
            @Override protected String featureValueOf(AuctionSniper sniper) {
                return sniper.getSnapshot().itemId();
            }
        };
    }
}
