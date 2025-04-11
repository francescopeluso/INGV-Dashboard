package ingv.dashboard;

import ingv.dashboard.model.INGVEvent;
import ingv.dashboard.model.INGVUtils;
import java.io.File;
import java.io.FileWriter;
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
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

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
        
        ContextMenu cm = new ContextMenu();
        MenuItem mi = new MenuItem("Esporta selezione");
        mi.setOnAction((ActionEvent event) -> {
            exportSelection();
        });
        cm.getItems().add(mi);
        
        eventsTable.getSelectionModel().setSelectionMode(
            SelectionMode.MULTIPLE
        );
        
        eventsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(eventsTable, t.getScreenX(), t.getScreenY());
                }
            }
        });
        
        
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
    
    private void exportSelection() {
        ObservableList<INGVEvent> selectedItems = eventsTable.getSelectionModel().getSelectedItems();
    
        if (selectedItems.isEmpty()) {
            System.out.println("Nessun elemento selezionato.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Esporta selezione");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(eventsTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {

                StringBuilder sb = new StringBuilder();
                sb.append("#EventID|Time|Latitude|Longitude|Depth/Km|Author|Catalog|Contributor|ContributorID|MagType|Magnitude|MagAuthor|EventLocationName\n");

                for (INGVEvent e : selectedItems) {
                    sb.append("" + e.getEventDatetime()).append("|")
                      .append(e.getMagnitude()).append("|")
                      .append(e.getLocation()).append("\n");
                }

                writer.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
