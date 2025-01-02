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
        int dateTimeDiff = this.eventDatetime.compareTo(o.eventDatetime);
        if (dateTimeDiff != 0)
            return dateTimeDiff;
        
        int magnitudeDiff = Double.compare(this.magnitude, o.magnitude);
        if (magnitudeDiff != 0)
            return magnitudeDiff;
        
        return this.location.compareToIgnoreCase(o.location);
    }

    @Override
    public String toString() {
        return "INGVEvent{" + "eventDatetime=" + eventDatetime + ", magnitude=" + magnitude + ", location=" + location + '}';
    }
    
}
