package nl.first8.auctions;

public class BidDeniedException extends Exception {
	private static final long serialVersionUID = 1L;

	public enum Reason {
		BID_TOO_LOW, NOT_LOGGED_IN
	}

	private final Reason reason;

	public BidDeniedException(Reason reason, String message) {
		super(message);
		this.reason = reason;
	}

	public Reason getReason() {
		return reason;
	}
}
