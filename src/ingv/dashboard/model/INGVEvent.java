package ingv.dashboard.model;

import java.time.LocalDateTime;

/**
 *
 * @author fp
 */
public class INGVEvent implements Comparable<INGVEvent> {
    
    private LocalDateTime eventDatetime;
    private double magnitude;
    private String location;
    
    public INGVEvent(LocalDateTime eventDatetime, double magnitude, String location) {
        this.eventDatetime = eventDatetime;
        this.magnitude = magnitude;
        this.location = location;
    }

    public LocalDateTime getEventDatetime() {
        return eventDatetime;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int compareTo(INGVEvent o) {
        return this.eventDatetime.compareTo(o.eventDatetime);
    }

    @Override
    public String toString() {
        return "INGVEvent{" + "eventDatetime=" + eventDatetime + ", magnitude=" + magnitude + ", location=" + location + '}';
    }
    
}
