package auctionsniper;

import auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMPPAuction implements Auction {
    private static final Logger logger = LoggerFactory.getLogger(XMPPAuction.class);
    public static final String AUCTION_RESOURCE = "Auction";

    private final Announcer<AuctionEventListener> auctionListeners =
            Announcer.to(AuctionEventListener.class);

    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        this.chat = connection
                .getChatManager()
                .createChat(
                        auctionID(itemId, connection.getServiceName()),
                        new AuctionMessageTranslator(connection.getUser(), auctionListeners.announce())
                );
    }

    @Override public void addListener(AuctionEventListener listener) {
        auctionListeners.addListener(listener);
    }

    @Override public void bid(int amount) {
        sendMessage(Main.BID_COMMAND_FORMAT.formatted(amount));
    }

    @Override public void join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(final String message) {
        logger.info("Sending msg to '" + chat.getParticipant() + "': " + message);
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private static String auctionID(String itemID, String serviceName) {
        String login = "auction-" + itemID;
        return "%s@%s/%s".formatted(login, serviceName, AUCTION_RESOURCE);
    }
}
