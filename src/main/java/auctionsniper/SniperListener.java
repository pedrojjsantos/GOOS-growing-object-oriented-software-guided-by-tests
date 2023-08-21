package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void updateSniperState(SniperSnapshot state); //sniperStateChanged
}
