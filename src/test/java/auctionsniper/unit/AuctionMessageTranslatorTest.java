package auctionsniper.unit;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.AuctionMessageTranslator;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

// TODO: 14/08/2023 Add error handling
public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;
    public static final String SNIPER_ID = "this Sniper's id";

    @RegisterExtension JUnit5Mockery context = new JUnit5Mockery();

    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

    @Test void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test @DisplayName("Notifies bid details when price message received from other bidder")
    void notifies_bid_details_when_price_message_received_from_other_bidder() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
        }});

        Message message = createPriceMessage(192, 7, "Someone else");
        translator.processMessage(UNUSED_CHAT, message);

    }

    @Test @DisplayName("Notifies bid details when price message received from sniper")
    void notifies_bid_details_when_price_message_received_from_sniper() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
        }});

        Message message = createPriceMessage(234, 5, SNIPER_ID);
        translator.processMessage(UNUSED_CHAT, message);
    }

    private static Message createPriceMessage(int price, int increment, String bidder) {
        Message message = new Message();
        String body = "SOLVersion: 1.1; Event: PRICE; " +
                "CurrentPrice: %d; Increment: %d; Bidder: %s;".formatted(price, increment, bidder);
        message.setBody(body);
        return message;
    }
}
