package ingv.dashboard.model;

import java.time.LocalDateTime;

/**
 *
 * @author fp
 */
public class INGVEvent implements Comparable<INGVEvent> {
    
    // #EventID|Time|Latitude|Longitude|Depth/Km|Author|Catalog|Contributor|ContributorID|MagType|Magnitude|MagAuthor|EventLocationName
    
    private String eventId;
    
    private LocalDateTime eventDatetime;
    
    private double latitude;
    private double longitude;
    private double depthKm;
    
    private String author;
    private String catalog;
    private String contributor;
    private String contributorId;
   
    private String magType;
    
    private double magnitude;
    private String magAuthor;
    
    private String location;
    
    public INGVEvent(String eventId, LocalDateTime eventDatetime, double latitude, double longitude, double depthKm, String author, String catalog, String contributor, String contributorId, String magType, double magnitude, String magAuthor, String location) {
        this.eventId = eventId;
        this.eventDatetime = eventDatetime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depthKm = depthKm;
        this.author = author;
        this.catalog = catalog;
        this.contributor = contributor;
        this.contributorId = contributorId;
        this.magType = magType;
        this.magnitude = magnitude;
        this.magAuthor = magAuthor;
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

    public String getEventId() {
        return eventId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDepthKm() {
        return depthKm;
    }

    public String getAuthor() {
        return author;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getContributor() {
        return contributor;
    }

    public String getContributorId() {
        return contributorId;
    }

    public String getMagType() {
        return magType;
    }

    public String getMagAuthor() {
        return magAuthor;
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
        return "INGVEvent{" + "eventId=" + eventId + ", eventDatetime=" + eventDatetime + ", latitude=" + latitude + ", longitude=" + longitude + ", depthKm=" + depthKm + ", author=" + author + ", catalog=" + catalog + ", contributor=" + contributor + ", contributorId=" + contributorId + ", magType=" + magType + ", magnitude=" + magnitude + ", magAuthor=" + magAuthor + ", location=" + location + '}';
    }
    
}
