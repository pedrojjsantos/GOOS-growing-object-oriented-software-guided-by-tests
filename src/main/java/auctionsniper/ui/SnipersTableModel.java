package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import static auctionsniper.ui.MainWindow.STATUS_JOINING;

public class SnipersTableModel extends AbstractTableModel {

    private String statusText = STATUS_JOINING;

    public void setStatusText(String statusText) {
        this.statusText = statusText;
        fireTableRowsUpdated(0, 0);
    }

    @Override public int getRowCount() {
        return 1;
    }

    @Override public int getColumnCount() {
        return 1;
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        return statusText;
    }
}
