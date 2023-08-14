package auctionsniper.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";

    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";


    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    public MainWindow() {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static JLabel createLabel(String initialText) {
        JLabel label = new JLabel(initialText);
        label.setName(SNIPER_STATUS_NAME);
        label.setBorder(new LineBorder(Color.BLACK));
        return label;
    }

    public void showStatus(String statusText) {
        sniperStatus.setText(statusText);
    }
}
