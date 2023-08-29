package e2e;

import auctionsniper.Main;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FakeAuctionServer {
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";

    private final SingleMessageListener messageListener = new SingleMessageListener();

    private final String itemId;
    private final String loginId;
    private final XMPPConnection connection;
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.loginId = "auction-" + itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public String getItemId() {
        return itemId;
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(this.loginId, AUCTION_PASSWORD, AUCTION_RESOURCE);

        connection.getChatManager().addChatListener(
                (chat, b) -> {
                    currentChat = chat;
                    chat.addMessageListener(messageListener);
                });
    }

    public void stop() {
        connection.disconnect();
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(Main.CLOSE_COMMAND_FORMAT);
    }

    public void reportPrice(int price, int increment, String winningBidder) throws XMPPException {
        currentChat.sendMessage(
                "SOLVersion: 1.1; Event: PRICE; " +
                "CurrentPrice: %d; Increment: %d; Bidder: %s;"
                        .formatted(price, increment, winningBidder)
        );
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.hasEntry()
                .withMessage(equalTo(Main.JOIN_COMMAND_FORMAT))
                .withParticipant(equalTo(ApplicationRunner.SNIPER_XMPP_ID));
    }

    public void hasReceivedBid(int bid, String xmppId) throws InterruptedException {
        messageListener.hasEntry()
                .withMessage(equalTo(Main.BID_COMMAND_FORMAT.formatted(bid)))
                .withParticipant(equalTo(xmppId));
    }

    public static class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Assertion> messages = new ArrayBlockingQueue<>(1);

        public void processMessage(Chat chat, Message msg) {
            messages.add(new Assertion(chat, msg));
        }

        private Assertion hasEntry() throws InterruptedException {
            Assertion entry = messages.poll(6, TimeUnit.SECONDS);
            assertThat("No message received", entry, is(notNullValue()));
            return entry;
        }

        static class Assertion {
            private final Chat chat;
            private final Message msg;

            Assertion(Chat chat, Message msg) {
                this.chat = chat;
                this.msg = msg;
            }

            public Assertion withMessage(Matcher<? super String> matcher) {
                assertThat(msg, hasProperty("body", matcher));
                return this;
            }

            public Assertion withParticipant(Matcher<? super String> matcher) {
                assertThat(chat.getParticipant(), matcher);
                return this;
            }
        }
    }
}
