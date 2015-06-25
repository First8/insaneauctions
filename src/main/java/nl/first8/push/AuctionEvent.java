package nl.first8.push;

import java.time.LocalDateTime;

public class AuctionEvent {

    private LocalDateTime timestamp;

    public AuctionEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
