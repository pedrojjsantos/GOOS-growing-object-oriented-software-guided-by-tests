package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private SniperState currentState = SniperState.initialState();

    @Override public int getRowCount() {
        return 1;
    }

    @Override public int getColumnCount() {
        return Column.values().length;
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(currentState);
    }

    @Override public void updateSniperState(SniperState newState) {
        currentState = newState;
        fireTableRowsUpdated(0,0);
    }

}
