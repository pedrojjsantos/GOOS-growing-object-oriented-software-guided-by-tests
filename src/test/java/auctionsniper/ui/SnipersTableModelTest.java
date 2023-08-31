package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
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

        SniperSnapshot joiningSnapshot = SniperSnapshot.joining("item id");
        SniperSnapshot biddingSnapshot = joiningSnapshot.bidding(555, 666);

        model.addSnapshot(joiningSnapshot);
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
        var joining = SniperSnapshot.joining("itemId");
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSnapshot(joining);

        assertEquals(1, model.getRowCount());

        assertRowMatchesSnapshot(0, joining);
    }

    @Test @DisplayName("Holds snipers in addition order")
    void holds_snipers_in_addition_order() {
        context.checking(new Expectations() { {
            ignoring(listener);
        }});

        model.addSnapshot(SniperSnapshot.joining("item 0"));
        model.addSnapshot(SniperSnapshot.joining("item 1"));
        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));

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