package ingv.dashboard;

import ingv.dashboard.model.INGVEvent;
import ingv.dashboard.model.INGVUtils;
import java.time.LocalDate;
import java.util.Set;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author fp
 */
public class DataLoadService extends Service<Set<INGVEvent>> {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private int minMagValue;
    private int maxMagValue;

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setMinMagValue(int minMagValue) {
        this.minMagValue = minMagValue;
    }

    public void setMaxMagValue(int maxMagValue) {
        this.maxMagValue = maxMagValue;
    }

    @Override
    protected Task<Set<INGVEvent>> createTask() {
        return new Task<Set<INGVEvent>>() {
            @Override
            protected Set<INGVEvent> call() throws Exception {
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("There are some missing parameters. Please check and try again.");
                }
                
                return INGVUtils.readFromINGV(startDate, endDate, minMagValue, maxMagValue);
            }
        };
    }
    
}
