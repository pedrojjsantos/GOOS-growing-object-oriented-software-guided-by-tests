package auctionsniper.integration;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import auctionsniper.util.AuctionSniperDriver;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test @DisplayName("Makes user request when join button clicked")
    void makes_user_request_when_join_button_clicked() {
        String itemId = "item-id";

        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item(itemId, 789)), "item request");

        mainWindow.addUserRequestListener(itemProbe::setReceivedValue);
        driver.startBiddingFor(itemId, 789);
        driver.check(itemProbe);
    }

    @AfterEach void tearDown() {
        driver.dispose();
    }
}
