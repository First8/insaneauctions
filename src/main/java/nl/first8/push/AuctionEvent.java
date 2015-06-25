package nl.first8.push;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuctionEvent implements Serializable {
	private static final long serialVersionUID = 1L;

    private LocalDateTime timestamp;

    public AuctionEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
