package auctionsniper.unit;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperPortfolio;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class SniperPortfolioTest {
    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final SniperPortfolio.PortfolioListener listener = context.mock(SniperPortfolio.PortfolioListener.class);
    private final SniperPortfolio portfolio = new SniperPortfolio();

    @Test @DisplayName("Notifies listeners of new snipers")
    void notifies_listeners_of_new_snipers() {
        AuctionSniper sniper = new AuctionSniper(null, "item id");

        context.checking(new Expectations() {{
            oneOf(listener).sniperWasAdded(sniper);
        }});

        portfolio.addListener(listener);
        portfolio.addSniper(sniper);
    }
}
