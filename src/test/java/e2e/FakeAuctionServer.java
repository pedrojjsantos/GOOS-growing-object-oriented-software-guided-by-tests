package e2e;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

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

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(this.loginId, AUCTION_PASSWORD, AUCTION_RESOURCE);

        connection.getChatManager().addChatListener(
                (chat, b) -> {
                    currentChat = chat;
                    chat.addMessageListener(messageListener);
                });
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    public static class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        public void processMessage(Chat chat, Message msg) {
            messages.add(msg);
        }

        public void receivesAMessage() throws InterruptedException {
            assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
        }
    }
}
