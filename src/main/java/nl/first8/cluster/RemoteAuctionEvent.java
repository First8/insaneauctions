package nl.first8.cluster;

import nl.first8.auctions.AuctionEvent;

public class RemoteAuctionEvent extends AuctionEvent {
	private static final long serialVersionUID = 1L;

	private String source;

	public RemoteAuctionEvent(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}
}
