package nl.first8.services;

import nl.first8.auctions.AuctionEvent;

public class RemoteAuctionEvent extends AuctionEvent {

    public String source;

    public RemoteAuctionEvent(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
