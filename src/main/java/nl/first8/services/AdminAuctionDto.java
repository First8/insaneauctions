package nl.first8.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import nl.first8.model.Auction;
import nl.first8.model.Bid;
import nl.first8.services.BidDto;
import nl.first8.services.ItemDto;

public class AdminAuctionDto {
    private final Long id;
    private final ItemDto item;
    private final Date dateCreated;
    private final Date dateEnd;
    private final Date dateClosed;
    private final List<BidDto> bids;

    public AdminAuctionDto(Auction auction, List<Bid> bids) {
    	this.id = auction.getId();
    	this.item = new ItemDto(auction.getItem());
    	this.dateClosed = auction.getDateClosed();
    	this.dateCreated = auction.getDateCreated();
    	this.dateEnd = auction.getDateEnd();
    	this.bids = bids.stream().map( b -> new BidDto(b)).collect(Collectors.toList());
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

}
