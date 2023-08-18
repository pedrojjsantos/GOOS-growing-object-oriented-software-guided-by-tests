package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperState;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private final SniperListener delegate;

    public SwingThreadSniperListener(SniperListener delegate) {
        this.delegate = delegate;
    }

    @Override public void updateSniperState(SniperState state) {
        SwingUtilities.invokeLater(
                () -> delegate.updateSniperState(state)
        );
    }

}
