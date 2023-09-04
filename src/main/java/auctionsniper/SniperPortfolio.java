package auctionsniper;

import auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class SniperPortfolio implements SniperCollector {
    private final Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);
    private final List<AuctionSniper> snipers = new ArrayList<>();


    public void addListener(PortfolioListener listener) {
        listeners.addListener(listener);
    }

    @Override public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperWasAdded(sniper);
    }

    public interface PortfolioListener extends EventListener {
        void sniperWasAdded(AuctionSniper sniper);
    }
}
