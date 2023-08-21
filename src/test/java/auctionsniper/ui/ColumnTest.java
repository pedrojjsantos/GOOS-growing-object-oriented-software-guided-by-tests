package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ColumnTest {

    @Test @DisplayName("Should extract the values from sniper state")
    void should_extract_the_values_from_sniper_state() throws Exception {
        final SniperSnapshot snapshot =
                new SniperSnapshot("item id", 123, 890, SniperState.BIDDING);

        assertThat(Column.ITEM_IDENTIFIER.valueIn(snapshot), equalTo("item id"));
        assertThat(Column.LAST_PRICE.valueIn(snapshot),      equalTo(123));
        assertThat(Column.LAST_BID.valueIn(snapshot),        equalTo(890));
        assertThat(Column.SNIPER_STATUS.valueIn(snapshot),   equalTo(SniperState.BIDDING.text()));
    }
}