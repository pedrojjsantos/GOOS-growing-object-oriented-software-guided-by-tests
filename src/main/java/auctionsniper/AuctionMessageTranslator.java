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
        Map<String, String> event = unpackEventFrom(message);
        String type = event.get("Event");

        switch (type) {
            case "CLOSE" -> listener.auctionClosed();
            case "PRICE" -> listener.currentPrice(
                    Integer.parseInt(event.get("CurrentPrice")),
                    Integer.parseInt(event.get("Increment"))
            );
            default -> throw new RuntimeException("Invalid Event:" + type + "!");
        }
    }

    private Map<String, String> unpackEventFrom(Message message) {
        HashMap<String, String> event = new HashMap<>();
        for (String element : message.getBody().split(";")) {
            String[] pair = element.split(":");
            event.put(pair[0].trim(), pair[1].trim());
        }
        return event;
    }
}
