package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String CLOSE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    public static final String AUCTION_RESOURCE = "Auction";

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();


        XMPPConnection connection = getConnection(
                args[ARG_HOSTNAME],
                args[ARG_USERNAME],
                args[ARG_PASSWORD]
        );

        main.disconnectWhenUICloses(connection);

        for (int i = ARG_ITEM_ID; i < args.length; i++) {
            main.joinAuction(connection, args[i]);
        }
    }

    @SuppressWarnings({"all"}) private List<Chat> oneChatToRuleThemAll = new ArrayList<>(); // Just to not let the chat get Garbage Collected

    private void joinAuction(XMPPConnection connection, String itemID) throws Exception {
        Chat chat = connection
                .getChatManager()
                .createChat(auctionID(itemID, connection.getServiceName()), null);

        Auction auction = new XMPPAuction(chat);
        SwingThreadSniperListener sniperListener = new SwingThreadSniperListener(snipers);
        chat.addMessageListener(new AuctionMessageTranslator(
                connection.getUser(),
                new AuctionSniper(auction, itemID, sniperListener)
        ));

        addSniperToModel(itemID);

        auction.join();
        sniperListener.updateSniperState(SniperSnapshot.joining(itemID));

        this.oneChatToRuleThemAll.add(chat);
    }

    private void addSniperToModel(String itemID) throws Exception {
        SwingUtilities.invokeAndWait(() -> snipers.addSniper(SniperSnapshot.joining(itemID)));
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
    ) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> this.ui = new MainWindow(snipers));
    }

    private static String auctionID(String itemID, String serviceName) {
        String login = "auction-" + itemID;
        return "%s@%s/%s".formatted(login, serviceName, AUCTION_RESOURCE);
    }


    public static class XMPPAuction implements Auction {
        private static final Logger logger = LoggerFactory.getLogger(XMPPAuction.class);
        private final Chat chat;

        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }

        public void bid(int amount) {
            sendMessage(BID_COMMAND_FORMAT.formatted(amount));
        }

        public void join() {
            sendMessage(JOIN_COMMAND_FORMAT);
        }

        private void sendMessage(final String message) {
            logger.info("Sending msg to '" + chat.getParticipant() + "': " + message);
            try {
                chat.sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

}