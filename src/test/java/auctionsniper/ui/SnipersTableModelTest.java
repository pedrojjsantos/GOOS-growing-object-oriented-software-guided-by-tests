package auctionsniper.ui;

import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SnipersTableModelTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final SnipersTableModel model = new SnipersTableModel();

    @Test @DisplayName("Has enough columns")
    void has_enough_columns() throws Exception {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }
}