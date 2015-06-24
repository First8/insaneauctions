package nl.first8.services;

import java.util.Date;

import nl.first8.auctions.BidDeniedException;

public class BidResultDto extends EventDto {
	private final boolean success;
	private final String reason;
	private final String message;
	private final Date dateAccepted;

	/** Success bid. */
	public BidResultDto(Date dateAccepted) {
		super("bid-result");
		this.dateAccepted = dateAccepted;
		this.success = true;
		this.message = null;
		this.reason = null;
	}

	/** Failed bid. */
	public BidResultDto(BidDeniedException ex) {
		super("bid-result");
		this.dateAccepted = null;
		this.success = false;
		this.message = ex.getMessage();
		this.reason = ex.getReason().name();
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public Date getDateAccepted() {
		return dateAccepted;
	}

	public String getReason() {
		return reason;
	}
}
