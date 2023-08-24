package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

class SnipersTableModelTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final TableModelListener listener = context.mock(TableModelListener.class);

    private final SnipersTableModel model = new SnipersTableModel();

    @BeforeEach
    void attach_listener_to_table_model() {
        model.addTableModelListener(listener);
    }

    @Test @DisplayName("Has enough columns")
    void has_enough_columns() throws Exception {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test @DisplayName("Updates sniper values in columns")
    void sets_sniper_values_in_columns() throws Exception {
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anInsertionAtRow(0)));
            oneOf(listener).tableChanged(with(aRowChangedEvent(0)));
        }});

        model.addSniper(SniperSnapshot.joining("item id"));
        model.updateSniperState(
                new SniperSnapshot("item id", 555, 666, SniperState.BIDDING)
        );

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATUS, SniperState.BIDDING.text());
    }

    @Test @DisplayName("Sets up column headings")
    void sets_up_column_headings() throws Exception {
        var columns = Column.values();
        for (int i = 0; i < columns.length; i++) {
            assertEquals(columns[i].title(), model.getColumnName(i));
        }
    }

    @Test @DisplayName("Notifies listener when adding a sniper")
    void notifies_listener_when_adding_a_sniper() throws Exception {
        var joining = SniperSnapshot.joining("itemId");
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());

        assertRowMatchesSnapshot(0, joining);
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