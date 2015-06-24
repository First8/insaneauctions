package nl.first8.services;

import nl.first8.model.Auction;
import nl.first8.model.Bid;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class AuctionDto extends EventDto {
    private final Long id;
    private final ItemDto item;
    private final Long timeRemaining;
    private final List<BidDto> bids;

    public AuctionDto(Auction auction, List<Bid> auctionBids) {
		super("auction");
    	this.id = auction.getId();
    	this.timeRemaining = auction.getDateEnd().getTime() - new Date().getTime();
    	this.item = new ItemDto(auction.getItem());
    	bids = auctionBids.stream().map( b -> new BidDto(b) ).collect(Collectors.toList());
    }

	public Long getId() {
		return id;
	}

	public ItemDto getItem() {
		return item;
	}

	public List<BidDto> getBids() {
		return bids;
	}

	public Long getTimeRemaining() {
		return timeRemaining;
	}
    
}
