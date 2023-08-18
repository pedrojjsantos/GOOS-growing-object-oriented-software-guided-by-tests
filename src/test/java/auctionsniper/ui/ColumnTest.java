package auctionsniper.ui;

import auctionsniper.SniperState;
import auctionsniper.SniperStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ColumnTest {

    @Test @DisplayName("Should extract the values from sniper state")
    void should_extract_the_values_from_sniper_state() throws Exception {
        final SniperState sniperState =
                new SniperState("item id", 123, 890, SniperStatus.BIDDING);

        assertThat(Column.ITEM_IDENTIFIER.valueIn(sniperState), equalTo("item id"));
        assertThat(Column.LAST_PRICE.valueIn(sniperState),      equalTo(123));
        assertThat(Column.LAST_BID.valueIn(sniperState),        equalTo(890));
        assertThat(Column.SNIPER_STATUS.valueIn(sniperState),   equalTo(SniperStatus.BIDDING.text()));
    }
}