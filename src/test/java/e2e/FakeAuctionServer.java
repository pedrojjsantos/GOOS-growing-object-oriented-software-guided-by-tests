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

    // TODO: 14/08/2023 Maybe refactor the message assertions to a builder
    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage(equalTo(Main.JOIN_COMMAND_FORMAT));
        assertThat(currentChat.getParticipant(), equalTo(ApplicationRunner.SNIPER_XMPP_ID));

    }

    public void hasReceivedBid(int bid, String from) throws InterruptedException {
        messageListener.receivesAMessage(
                equalTo(Main.BID_COMMAND_FORMAT.formatted(bid))
        );
        assertThat(currentChat.getParticipant(), equalTo(from));
    }

    public static class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        public void processMessage(Chat chat, Message msg) {
            System.out.println(msg.getSubject() + ": " + msg.getBody());
            messages.add(msg);
        }

        public void receivesAMessage(Matcher<? super String> matcher) throws InterruptedException {
            Message msg = messages.poll(5, TimeUnit.SECONDS);
            assertThat("Message", msg, is(notNullValue()));
            assertThat(msg.getBody(), matcher);
        }
    }
}
