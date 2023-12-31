package auctionsniper.util;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.any;

public class AuctionSniperDriver extends JFrameDriver {
    static { System.setProperty("com.objogate.wl.keyboard", "US"); }

    public AuctionSniperDriver(int timeout) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()
                ),
                new AWTEventQueueProber(timeout, 100)
        );
    }

    public void showsSniperStatus(String itemId, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching(
                withLabelText(itemId),
                withLabelText(any(String.class)),
                withLabelText(any(String.class)),
                withLabelText(statusText)
        ));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching(
                        withLabelText(itemId),
                        withLabelText(lastPrice + ""),
                        withLabelText(lastBid + ""),
                        withLabelText(statusText)
        ));
    }

    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(
                withLabelText("Item"),
                withLabelText("Last Price"),
                withLabelText("Last Bid"),
                withLabelText("State")
        ));
    }



    public void startBiddingFor(String itemId) {
        typeInField(itemIdField(), itemId);
        typeInField(stopPriceField(), Integer.MAX_VALUE + "");

        bidButton().click();
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        typeInField(itemIdField(), itemId);
        typeInField(stopPriceField(), stopPrice + "");

        bidButton().click();
    }

    private void typeInField(JTextFieldDriver textField, String text) {
        clear(textField);
        textField.replaceAllText(text);
    }

    private void clear(JTextFieldDriver textField) {
        textField.component().component().setText("");
    }

    private JTextFieldDriver itemIdField() {
        return textField(MainWindow.NEW_ITEM_ID_NAME);
    }

    private JTextFieldDriver stopPriceField() {
        return textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME);
    }

    private JTextFieldDriver textField(String fieldId) {
        JTextFieldDriver newItemId =
                new JTextFieldDriver(this, JTextField.class, named(fieldId));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }
}
