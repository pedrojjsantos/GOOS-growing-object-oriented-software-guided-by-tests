package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID  = 3;

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
        Chat chat = connection
                .getChatManager()
                .createChat(auctionID(itemID, connection.getServiceName()),
                        (aChat, msg) -> SwingUtilities.invokeLater(
                                () -> this.ui.showStatus(MainWindow.STATUS_LOST)
                        )
                );
        chat.sendMessage(new Message());
        this.aChat = chat;
    }

    private static XMPPConnection getConnection(
            String hostname, String username, String password
    ) throws XMPPException
    {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password);
        return connection;
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait( () -> this.ui = new MainWindow() );
    }

    private static String auctionID(String itemID, String serviceName) {
        String login = "auction-" + itemID;
        return "%s@%s/%s".formatted(login, serviceName, AUCTION_RESOURCE);
    }

//    public static void main(String xmttpHostname, String sniperId, String sniperPassword, String itemId) {
//    }
}