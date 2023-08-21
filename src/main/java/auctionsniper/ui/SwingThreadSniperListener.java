package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private final SniperListener delegate;

    public SwingThreadSniperListener(SniperListener delegate) {
        this.delegate = delegate;
    }

    @Override public void updateSniperState(SniperSnapshot state) {
        SwingUtilities.invokeLater(
                () -> delegate.updateSniperState(state)
        );
    }

}
