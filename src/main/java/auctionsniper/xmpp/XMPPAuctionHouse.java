package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
    public static final String AUCTION_RESOURCE = "Auction";

    private final XMPPConnection connection;

    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    @Override public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionID(itemId, connection.getServiceName()));
    }

    public void disconnect() {
        connection.disconnect();
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return new XMPPAuctionHouse(connection);
    }

    private static String auctionID(String itemID, String serviceName) {
        String login = "auction-" + itemID;
        return "%s@%s/%s".formatted(login, serviceName, AUCTION_RESOURCE);
    }
}
