package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void updateSniperState(SniperState state); //sniperStateChanged
}
