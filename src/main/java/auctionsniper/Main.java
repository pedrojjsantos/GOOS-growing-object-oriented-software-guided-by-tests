package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main implements AuctionEventListener {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID  = 3;

    public static final String JOIN_COMMAND_FORMAT  = "SOLVersion: 1.1; Command: JOIN;";
    public static final String CLOSE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
    public static final String BID_COMMAND_FORMAT   = "SOLVersion: 1.1; Command: BID; Price: %d;";

    public static final String AUCTION_RESOURCE = "Auction";

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();

        main.joinAuction(
                getConnection(
                        args[ARG_HOSTNAME],
                        args[ARG_USERNAME],
                        args[ARG_PASSWORD]
                ),
                args[ARG_ITEM_ID]
        );
    }

    @SuppressWarnings("unused") private Chat aChat; // To not be Garbage Collected
    private void joinAuction(XMPPConnection connection, String itemID) throws XMPPException {
        disconnectWhenUICloses(connection);

        Chat chat = connection
                .getChatManager()
                .createChat(
                        auctionID(itemID, connection.getServiceName()),
                        new AuctionMessageTranslator(this)
                );
        this.aChat = chat;
        chat.sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static XMPPConnection getConnection(
            String hostname, String username, String password
    ) throws XMPPException
    {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait( () -> this.ui = new MainWindow() );
    }

    private static String auctionID(String itemID, String serviceName) {
        String login = "auction-" + itemID;
        return "%s@%s/%s".formatted(login, serviceName, AUCTION_RESOURCE);
    }

    @Override
    public void auctionClosed() {
        SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_LOST));
    }

    @Override
    public void currentPrice(int currentPrice, int increment) {
        SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_BIDDING));
    }
}