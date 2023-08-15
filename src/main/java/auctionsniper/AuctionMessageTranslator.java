package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements MessageListener {
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    @Override public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        switch (event.type()) {
            case "CLOSE" -> listener.auctionClosed();
            case "PRICE" -> listener.currentPrice(event.currentPrice(), event.increment());
            default -> throw new RuntimeException("Invalid Event:" + event.type() + "!");
        }
    }

    private static class AuctionEvent {
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
