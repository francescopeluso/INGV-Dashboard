package ingv.dashboard;

import ingv.dashboard.model.INGVEvent;
import ingv.dashboard.model.INGVUtils;
import java.io.IOException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author fp
 */
public class DashboardFXMLController implements Initializable {
    
    @FXML
    private TableView<INGVEvent> eventsTable;
    @FXML
    private Button loadDataButton;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableColumn<INGVEvent, String> dateClmn;
    @FXML
    private TableColumn<INGVEvent, String> magnitudeClmn;
    @FXML
    private TableColumn<INGVEvent, String> locationClmn;
    @FXML
    private Button emptyData;
    @FXML
    private TextField minMagnitudeField;
    @FXML
    private TextField maxMagnitudeField;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private TextField locationFilterField;
    
    private ObservableList<INGVEvent> eventsHistory;
    private FilteredList<INGVEvent> filteredEvents;
    private final DataLoadService dataLoadService = new DataLoadService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding allFieldsFilled = startDatePicker.valueProperty().isNotNull().and(endDatePicker.valueProperty().isNotNull());
        loadDataButton.disableProperty().bind(allFieldsFilled.not());
        
        eventsHistory = FXCollections.observableArrayList();
        filteredEvents = new FilteredList<>(eventsHistory, p -> true);
        
        eventsTable.setItems(filteredEvents);
        
        dateClmn.setCellValueFactory(new PropertyValueFactory("eventDatetime"));
        magnitudeClmn.setCellValueFactory(new PropertyValueFactory("magnitude"));
        locationClmn.setCellValueFactory(new PropertyValueFactory("location"));
        
        startDatePicker.setValue(LocalDate.now().minusDays(7));
        endDatePicker.setValue(LocalDate.now());
        
        minMagnitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                minMagnitudeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            
            if (newValue.length() == 0) {
                minMagnitudeField.setText("0");
            }
        });
        
        maxMagnitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxMagnitudeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            
            if (newValue.length() == 0) {
                maxMagnitudeField.setText("0");
            }
        });
        
        locationFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEvents.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return event.getLocation().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        dataLoadService.setOnSucceeded(workerStateEvent -> {
            eventsHistory.setAll(dataLoadService.getValue());
        });
        
        dataLoadService.setOnFailed(workerStateEvent -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("An error occurred while retrieving data. Try again.");
            errorAlert.show();
        });
        
        loadingIndicator.visibleProperty().bind(dataLoadService.runningProperty());
        
        startDatePicker.disableProperty().bind(dataLoadService.runningProperty());
        endDatePicker.disableProperty().bind(dataLoadService.runningProperty());
        minMagnitudeField.disableProperty().bind(dataLoadService.runningProperty());
        maxMagnitudeField.disableProperty().bind(dataLoadService.runningProperty());
        loadDataButton.disableProperty().bind(dataLoadService.runningProperty());
        emptyData.disableProperty().bind(dataLoadService.runningProperty());
    }    

    @FXML
    private void loadDataAction(ActionEvent event) {
        try {
            dataLoadService.setStartDate(startDatePicker.getValue());
            dataLoadService.setEndDate(endDatePicker.getValue());
            dataLoadService.setMinMagValue(Integer.parseInt(minMagnitudeField.getText()));
            dataLoadService.setMaxMagValue(Integer.parseInt(maxMagnitudeField.getText()));

            if (dataLoadService.getState() == Worker.State.RUNNING) {
                dataLoadService.cancel();
            }

            if (dataLoadService.getState() == Worker.State.SUCCEEDED ||
                dataLoadService.getState() == Worker.State.CANCELLED ||
                dataLoadService.getState() == Worker.State.FAILED) {
                dataLoadService.reset();
            }
            
            dataLoadService.start();
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Invalid input parameters. Please check your inputs.");
            errorAlert.show();
        }
    }

    @FXML
    private void emptyDataAction(ActionEvent event) {
        eventsHistory.clear();
    }
    
}
