package auctionsniper.ui;

import auctionsniper.*;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, SniperCollector {
    @SuppressWarnings({"all"}) private final List<AuctionSniper> oneSniperToRuleThemAll = new ArrayList<AuctionSniper>(); // Just to not let the auctions get Garbage Collected

    private final List<SniperSnapshot> snapshots = new ArrayList<>();

    @Override public String getColumnName(int column) {
        return Column.at(column).title();
    }

    @Override public int getRowCount() {
        return snapshots.size();
    }

    @Override public int getColumnCount() {
        return Column.values().length;
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override public void updateSniperState(SniperSnapshot snapshot) {
        int row = rowMatching(snapshot);
        snapshots.set(row, snapshot);

        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).hasSameItemAs(snapshot)) {
                return i;
            }
        }
        throw new RuntimeException("Cannot find match for " + snapshot);
    }

    public void addSnapshot(SniperSnapshot snapshot) {
        int row = snapshots.size();
        snapshots.add(snapshot);
        fireTableRowsInserted(row, row);
    }

    @Override public void addSniper(AuctionSniper sniper) {
        addSnapshot(sniper.getSnapshot());
        sniper.addListener(this);

        oneSniperToRuleThemAll.add(sniper);
    }
}
