package nl.first8.auctions;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class CloseAuctionTask implements Runnable {
	private static final Logger log = Logger.getLogger(CloseAuctionTask.class.getName());

	@Inject
	private Auctioneer auctioneer;

	@Override
	public void run() {
		log.info("Closing auction");
		auctioneer.closeAuction();
	}
}
