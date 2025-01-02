package ingv.dashboard.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author fp
 */
public class INGVUtils {
    
    public static Set<INGVEvent> readFromINGV(LocalDate startDate, LocalDate endDate, Integer minMagnitude, Integer maxMagnitude) throws IOException {
        
        String apiURL = "https://webservices.ingv.it/fdsnws/event/1/query?format=text"
                + "&starttime=" + startDate
                + "&endtime=" + endDate;
        
        if (minMagnitude != null && minMagnitude >= 0) {
            apiURL += "&minmag=" + minMagnitude;
        }
        
        if (maxMagnitude != null && maxMagnitude >= 0) {
            apiURL += "&maxmag=" + maxMagnitude;
        }
        
        System.out.println("Retrieving data from: " + apiURL);
        
        URL apiEndpoint = new URL(apiURL);
        
        Set<INGVEvent> streamHistory = new TreeSet<>();
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(apiEndpoint.openStream()))) {
            String inputLine;
            boolean isHeader = true;
            
            while ((inputLine = in.readLine()) != null) {
                if (isHeader)
                    isHeader = false;
                else {
                    String[] columns = inputLine.split("\\|");
                    
                    String date = columns[1];
                    String magnitude = columns[10];
                    String location = columns[12];
                    
                    streamHistory.add(new INGVEvent(LocalDateTime.parse(date), Double.parseDouble(magnitude), location));
                }
            }
            
            return streamHistory;
        }
        
    }
    
}
