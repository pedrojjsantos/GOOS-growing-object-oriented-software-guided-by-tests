package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost();
    void sniperIsBidding(SniperState state);
    void sniperIsWinning();
    void sniperWon();
}
