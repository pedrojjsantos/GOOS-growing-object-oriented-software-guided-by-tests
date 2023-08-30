package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(XMPPAuction.class);

    private final AuctionEventListener listener;
    private final String sniperId;


    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.listener = listener;
        this.sniperId = sniperId;
    }

    @Override public void processMessage(Chat chat, Message message) {
        logger.info("Processing message: '" + message.getBody() + "'");
        AuctionEvent event = AuctionEvent.from(message.getBody());

        switch (event.type()) {
            case "CLOSE" -> listener.auctionClosed();
            case "PRICE" -> sendCurrentPrice(event.currentPrice(), event.increment(), event.bidder());
            default -> throw new RuntimeException("Invalid Event:" + event.type() + "!");
        }
    }

    private void sendCurrentPrice(int price, int increment, String src) {
        AuctionEventListener.PriceSource source = src.equals(this.sniperId) ?
                AuctionEventListener.PriceSource.FromSniper : AuctionEventListener.PriceSource.FromOtherBidder;

        listener.currentPrice(price, increment, source);
    }

    protected static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();

        public String type() {
            return fields.get("Event");
        }
        public int currentPrice() {
            return getInt("CurrentPrice");
        }
        public int increment() {
            return getInt("Increment");
        }
        public String bidder() {
            return fields.get("Bidder");
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(fields.get(fieldName));
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();

            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
    }
}
