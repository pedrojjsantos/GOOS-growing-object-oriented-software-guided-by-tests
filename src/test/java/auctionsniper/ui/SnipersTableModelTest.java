package auctionsniper.ui;

import auctionsniper.SniperState;
import auctionsniper.SniperStatus;
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

    @Test @DisplayName("Sets sniper values in columns")
    void sets_sniper_values_in_columns() throws Exception {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(aRowChangedEvent()));
        }});

        model.updateSniperState(
                new SniperState("item id", 555, 666, SniperStatus.BIDDING)
        );

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATUS, SniperStatus.BIDDING.text());
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}