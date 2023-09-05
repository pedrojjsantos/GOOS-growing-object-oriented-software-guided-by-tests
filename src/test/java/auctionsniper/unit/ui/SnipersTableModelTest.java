package auctionsniper.unit.ui;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SnipersTableModelTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final TableModelListener listener = context.mock(TableModelListener.class);

    private final SnipersTableModel model = new SnipersTableModel();

    private final Auction nullAuction = new Auction() {
        @Override public void bid(int amount) {

        }

        @Override public void join() {

        }

        @Override public void addListener(AuctionEventListener listener) {

        }
    };

    @BeforeEach
    void attach_listener_to_table_model() {
        model.addTableModelListener(listener);
    }

    @Test @DisplayName("Has enough columns")
    void has_enough_columns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test @DisplayName("Updates sniper values in columns")
    void sets_sniper_values_in_columns() {
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anInsertionAtRow(0)));
            oneOf(listener).tableChanged(with(aRowChangedEvent(0)));
        }});

        AuctionSniper sniper = createSniper("item-id");
        SniperSnapshot biddingSnapshot = sniper.getSnapshot().bidding(555, 666);

        model.sniperWasAdded(sniper);
        model.updateSniperState(biddingSnapshot);

        assertRowMatchesSnapshot(0, biddingSnapshot);
    }

    @Test @DisplayName("Sets up column headings")
    void sets_up_column_headings() {
        var columns = Column.values();
        for (int i = 0; i < columns.length; i++) {
            assertEquals(columns[i].title(), model.getColumnName(i));
        }
    }

    @Test @DisplayName("Notifies listener when adding a sniper")
    void notifies_listener_when_adding_a_sniper() {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        AuctionSniper sniper = createSniper("item-id");
        model.sniperWasAdded(sniper);

        assertEquals(1, model.getRowCount());

        assertRowMatchesSnapshot(0, sniper.getSnapshot());
    }

    @Test @DisplayName("Holds snipers in addition order")
    void holds_snipers_in_addition_order() {
        context.checking(new Expectations() { {
            ignoring(listener);
        }});

        String itemId0 = "item 0";
        String itemId1 = "item 1";

        model.sniperWasAdded(createSniper(itemId0));
        model.sniperWasAdded(createSniper(itemId1));
        assertEquals(itemId0, cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals(itemId1, cellValue(1, Column.ITEM_IDENTIFIER));

    }

    private AuctionSniper createSniper(String itemId) {
        return new AuctionSniper(nullAuction, itemId);
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId(), cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice(), cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid(), cellValue(row, Column.LAST_BID));
        assertEquals(snapshot.statusText(), cellValue(row, Column.SNIPER_STATUS));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }


    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(
                new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)
        );
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangedEvent(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }
}