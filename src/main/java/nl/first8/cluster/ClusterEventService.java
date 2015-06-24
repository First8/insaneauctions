package nl.first8.cluster;

import nl.first8.auctions.AuctionEvent;

import javax.ejb.Local;

@Local
public interface ClusterEventService {

    void receiveEvent(RemoteAuctionEvent event);

    void onAuctionUpdated(AuctionEvent auctionEvent);
}