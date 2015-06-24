package nl.first8.auctions;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import nl.first8.data.AuctionRepository;
import nl.first8.model.Amount;
import nl.first8.model.Auction;
import nl.first8.model.Bid;
import nl.first8.model.Item;

@Singleton
public class AuctioneerImpl implements Auctioneer {
	private static final Logger log = Logger.getLogger(AuctioneerImpl.class.getName());

	@Inject
	private AuctionRepository auctionRepository;

	@Resource
	private ManagedScheduledExecutorService executorService;

	@PostConstruct
	private void startup() {
		long newAuction = newAuction().getId();
		log.info("Starting up, auctioning a new auction: " + newAuction);
	}

	private Auction newAuction() {
		List<Item> items = auctionRepository.getAllItems();
		int i = ThreadLocalRandom.current().nextInt(items.size());
		Item item = items.get(i);

		Date dateStart = new Date();
		Date dateEnd = new Date(dateStart.getTime() + item.getAuctionTime());

		Auction auction = auctionRepository.persist(new Auction(item, dateStart, dateEnd));
		log.info("New auction started for item id " + auction.getItem().getId() + ": " + auction.getId());

		return auction;
	}

	@Override
	@Transactional(TxType.REQUIRES_NEW)
	public Auction closeAuction() {
		Auction auction = getCurrentAuction();
		auction.setDateClosed(new Date());
		return newAuction();
	}

	@Override
	public Auction getCurrentAuction() {
		Auction auction = auctionRepository.findLatestAuction();
		log.info("Current auction id: " + auction.getId());
		return auction;
	}

	@Override
	public void acceptBid(Amount amount, String nickname) throws BidDeniedException {
		Auction auction = getCurrentAuction();

		if (!auction.getBids().isEmpty()) {
			verifyLastBid(amount, auction);
		}

		Bid bid = new Bid(auction, amount, nickname);
		auction.addBid(bid);
		auctionRepository.persist(bid);
	}

	private void verifyLastBid(Amount amount, Auction auction) throws BidDeniedException {
		Bid lastBid = auctionRepository.getBidsSortedByDate(auction).get(0);
		if (lastBid.getAmount().compareTo(amount) >= 0) {
			throw new BidDeniedException(BidDeniedException.Reason.BID_TOO_LOW, //
					"Already bid of " + lastBid.getAmount() + " which is higher or equal than " + amount);
		}
	}
}
