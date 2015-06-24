package nl.first8.auctions;

import nl.first8.model.Amount;
import nl.first8.model.Auction;

public interface Auctioneer {
	Auction getCurrentAuction();
	void acceptBid(Amount amount, String nickname) throws BidDeniedException;
	Auction closeAuction();
}
