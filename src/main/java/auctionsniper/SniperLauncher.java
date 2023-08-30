package auctionsniper;

import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import auctionsniper.ui.UserRequestListener;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener {
    @SuppressWarnings({"all"}) private final List<Auction> oneAuctionToRuleThemAll = new ArrayList<>(); // Just to not let the auctions get Garbage Collected
    private final AuctionHouse auctionHouse;
    private final SnipersTableModel snipers;

    public SniperLauncher(AuctionHouse auctionHouse, SnipersTableModel snipers) {
        this.auctionHouse = auctionHouse;
        this.snipers = snipers;
    }

    @Override public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));

        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(auction, itemId, new SwingThreadSniperListener(snipers));

        auction.addListener(sniper);
        auction.join();

        oneAuctionToRuleThemAll.add(auction);
    }
}
