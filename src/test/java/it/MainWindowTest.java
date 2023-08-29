package it;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import e2e.AuctionSniperDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test @DisplayName("Makes user request when join button clicked")
    void makes_user_request_when_join_button_clicked() {
        String itemId = "item-id";

        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<>(equalTo(itemId), "join request");

        mainWindow.addUserRequestListener(buttonProbe::setReceivedValue);
        driver.startBiddingFor(itemId);
        driver.check(buttonProbe);
    }
}
