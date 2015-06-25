package nl.first8.services;

import java.io.Serializable;

public class EventDto implements Serializable {

    private String event;

    public EventDto(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
