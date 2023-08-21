package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private SniperSnapshot currentState = SniperSnapshot.initialState();

    @Override public String getColumnName(int column) {
        return Column.at(column).title();
    }

    @Override public int getRowCount() {
        return 1;
    }

    @Override public int getColumnCount() {
        return Column.values().length;
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(currentState);
    }

    @Override public void updateSniperState(SniperSnapshot newState) {
        currentState = newState;
        fireTableRowsUpdated(0,0);
    }

}
