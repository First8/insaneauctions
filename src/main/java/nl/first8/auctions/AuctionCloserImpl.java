package nl.first8.auctions;

import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import nl.first8.model.Auction;

@Singleton
public class AuctionCloserImpl implements AuctionCloser {
	private static final Logger LOG = Logger.getLogger(AuctionCloserImpl.class.getName());

	@Inject
	private Auctioneer auctioneer;

	@Inject
	@AuctionUpdated
	private Event<AuctionEvent> auctionUpdatedEvent;

	@Resource
	private TimerService timerService;

	@Timeout
	public void scheduler(Timer timer) {
		LOG.info(timer.getInfo() + ", closing auction");
		Auction newAuction = auctioneer.closeAuction();
		LOG.info("Fire auction updated event.");
		auctionUpdatedEvent.fire(new AuctionEvent());
		LOG.info("Scheduling new timer at " + newAuction.getDateEnd());
		timerService.createSingleActionTimer(newAuction.getDateEnd(), new TimerConfig(timer.getInfo(), false));
	}

	@Override
	public void initialize(String info) {
		Date dateEnd = auctioneer.getCurrentAuction().getDateEnd();
		LOG.info("Scheduling timer at " + dateEnd);
		timerService.createSingleActionTimer(dateEnd, new TimerConfig(info, false));
	}

	@Override
	public void stop() {
		LOG.info("Stop all existing AuctionCloserImpl timers");
		for (Timer timer : timerService.getTimers()) {
			LOG.finest("Stop AuctionCloserImpl timer: " + timer.getInfo());
			timer.cancel();
		}
	}
}
