package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    private static final Logger logger = LoggerFactory.getLogger(XMPPAuction.class);

    private final Announcer<AuctionEventListener> auctionListeners =
            Announcer.to(AuctionEventListener.class);

    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        this.chat = connection
                .getChatManager()
                .createChat(itemId, new AuctionMessageTranslator(
                        connection.getUser(),
                        auctionListeners.announce()
                ));
    }

    @Override public void addListener(AuctionEventListener listener) {
        auctionListeners.addListener(listener);
    }

    @Override public void bid(int amount) {
        sendMessage(BID_COMMAND_FORMAT.formatted(amount));
    }

    @Override public void join() {
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
