package e2e;

import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeout) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MAIN_WINDOW_NAME),

                ));
    }

    public void showsSniperStatus(String status) {
        throw new UnsupportedOperationException("TODO");
    }

    public void dispose() {
        throw new UnsupportedOperationException("TODO");
    }
}
