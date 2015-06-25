package nl.first8.auctions;

import nl.first8.push.AuctionEvent;
import nl.first8.push.AuctionUpdated;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class CloseAuctionTask implements Runnable {
	private static final Logger log = Logger.getLogger(CloseAuctionTask.class.getName());

	@Inject
	private Auctioneer auctioneer;

	@Inject
	@AuctionUpdated
	private Event<AuctionEvent> auctionUpdatedEvent;

	@Override
	public void run() {
		log.info("Closing auction");
		auctioneer.closeAuction();
		log.info("Fire auction updated event.");
		auctionUpdatedEvent.fire(new AuctionEvent());
	}
}
