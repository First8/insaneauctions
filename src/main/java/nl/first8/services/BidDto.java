package nl.first8.services;

import nl.first8.model.Amount;
import nl.first8.model.Bid;

import java.util.Date;

public class BidDto {

	private final Long id;
	private final Date dateCreated;
	private final Amount amount;
	private final String bidderName;

	public BidDto(Bid bid) {
		this(bid.getId(), bid.getDateCreated(), bid.getAmount(), bid.getBidderName());
	}

	public BidDto(Long id, Date dateCreated, Amount amount, String bidderName) {
		this.id = id;
		this.dateCreated = dateCreated;
		this.amount = amount;
		this.bidderName = bidderName;
	}

	public Long getId() {
		return id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public Amount getAmount() {
		return amount;
	}

	public String getBidderName() {
		return bidderName;
	}

}
