package ingv.dashboard.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 *
 * @author fp
 */
public class INGVUtils {

    public static Set<INGVEvent> readFromINGV(String remoteURL, LocalDate startDate, LocalDate endDate, Integer minMagnitude, Integer maxMagnitude, Integer limit, Consumer<Double> progressCallback) throws IOException, InterruptedException {
    
        String apiURL = remoteURL // "https://webservices.ingv.it/fdsnws/event/1/query?format=text"
                + "&starttime=" + startDate
                + "&endtime=" + endDate;

        if (minMagnitude != null && minMagnitude >= 0) {
            apiURL += "&minmag=" + minMagnitude;
        }

        if (maxMagnitude != null && maxMagnitude >= 0) {
            apiURL += "&maxmag=" + maxMagnitude;
        }

        if (limit != null && limit > 0) {
            apiURL += "&limit=" + limit;
        }

        System.out.println("Retrieving data from: " + apiURL);
        URL apiEndpoint = new URL(apiURL);
        Set<INGVEvent> streamHistory = new TreeSet<>();

        // conto le righe effettive dentro allo stream
        int linesCount = 0;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(apiEndpoint.openStream()))) {
            String line;
            boolean isHeader = true;

            while ((line = in.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                } else {
                    linesCount++;
                }
            }
        }

        progressCallback.accept(0.0);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(apiEndpoint.openStream()))) {
            String inputLine;
            boolean isHeader = true;
            int linesProcessed = 0;

            while ((inputLine = in.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                } else {
                    String[] columns = inputLine.split("\\|");
                    
                    String eventId = columns[0];
                    LocalDateTime eventDatetime = LocalDateTime.parse(columns[1]);
                    double latitude = Double.parseDouble(columns[2]);
                    double longitude = Double.parseDouble(columns[3]);
                    double depthKm = Double.parseDouble(columns[4]);
                    String author = columns[5];
                    String catalog = columns[6];
                    String contributor = columns[7];
                    String contributorId = columns[8];
                    String magType = columns[9];
                    double magnitude = Double.parseDouble(columns[10]);
                    String magAuthor = columns[11];
                    String location = columns[12];

                    streamHistory.add(new INGVEvent(
                        eventId, eventDatetime, latitude, longitude, depthKm, 
                        author, catalog, contributor, contributorId, 
                        magType, magnitude, magAuthor, location));
                    
                    linesProcessed++;

                    if (linesCount > 0) {
                        double progress = (double) linesProcessed / linesCount;
                        System.out.println("actual progress: " + progress);
                        progressCallback.accept(progress);
                    }
                }
                
                // aggiungo per rendere visibile il progress indicator
                Thread.sleep(100);
            }

            progressCallback.accept(1.0);

            return streamHistory;
        }
    }
}
