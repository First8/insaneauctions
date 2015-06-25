package nl.first8.services;

import nl.first8.auctions.Auctioneer;
import nl.first8.auctions.BidDeniedException;
import nl.first8.auctions.BidDeniedException.Reason;
import nl.first8.data.AuctionRepository;
import nl.first8.model.Amount;
import nl.first8.model.Auction;
import nl.first8.model.Bid;
import nl.first8.push.AuctionEvent;
import nl.first8.push.AuctionUpdated;
import nl.first8.push.PushService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class AuctionService {
	private static final Logger log = Logger.getLogger(AuctionService.class.getName());
	private static final int MAX_BIDS = 5;
	
	@Inject
	private Auctioneer auctioneer;
	@Inject
	private AuctionRepository auctionRepository;

	@Inject
	private Client client;

	@Inject
	private PushService pushService;

	@Inject @AuctionUpdated
	private Event<AuctionEvent> auctionUpdatedEvent;

	public void onAuctionClosed(@Observes @AuctionUpdated AuctionEvent auctionEvent) {
		pushService.push(getActiveAuction());
	}

	public AuctionDto getActiveAuction() {
		Auction auction = auctioneer.getCurrentAuction();
		
		List<Bid> bids = auctionRepository.getBidsSortedByDate(auction);
		if (bids.size()>MAX_BIDS) {
			bids = bids.subList(0,  MAX_BIDS);
		}
		return new AuctionDto(auction, bids);
	}

	public BidResultDto bid(MakeBidDto makeBid) {
		try {
			if (client.isLoggedIn()) {
				Amount amount = new Amount(makeBid.getValue(), makeBid.getCurrency());
				String nickname = client.getNickname();
				log.info("Making bid with amount " + amount + " for user " + nickname);
				auctioneer.acceptBid(
						amount,
						nickname);
				auctionUpdatedEvent.fire(new AuctionEvent());
			} else {
				throw new BidDeniedException(Reason.NOT_LOGGED_IN,
						"You are not logged in.");
			}
			return new BidResultDto(new Date());
		} catch (BidDeniedException e) {
			return new BidResultDto(e);
		}
	}

	public List<BidDto> getBids() {
		Auction auction = auctioneer.getCurrentAuction();
		List<Bid> bids = auctionRepository.getBidsSortedById(auction);
		log.info("Returning " + bids.size() + " bids for current auction id "
				+ auction.getId());
		List<BidDto> result = new ArrayList<>();
		for (Bid b : bids) {
			result.add(new BidDto(b));
		}
		return result;
	}
}
