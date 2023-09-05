package auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Formatter;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "SNIPERS_TABLE_NAME";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join auction";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio sniperPortfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(sniperPortfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addListener(model);

        JTable table = new JTable(model);
        table.setName(SNIPERS_TABLE_NAME);

        return table;
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        var itemIdField = new JTextField();
        itemIdField.setColumns(10);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(label("Item:"));
        controls.add(itemIdField);

        var stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        stopPriceField.setColumns(8);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(label("Stop Price:"));
        controls.add(stopPriceField);

        var joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener( (event) ->
                userRequests.announce().joinAuction(new Item(
                        itemIdField.getText(),
                        ((Number)stopPriceField.getValue()).intValue()
                ))
        );
        controls.add(joinAuctionButton);

        return controls;
    }

    private static JLabel label(String text) {
        JLabel itemIdLabel = new JLabel();
        itemIdLabel.setText(text);
        return itemIdLabel;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
}
