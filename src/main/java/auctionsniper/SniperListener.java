package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost();
    void sniperIsBidding();
    void sniperIsWinning();
    void sniperWon();
}
