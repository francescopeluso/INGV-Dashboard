/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ingv.dashboard;

import ingv.dashboard.model.INGVEvent;
import ingv.dashboard.model.INGVUtils;
import java.io.IOException;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    
    private ObservableList<INGVEvent> eventsHistory;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding allFieldsFilled = startDatePicker.valueProperty().isNotNull().and(endDatePicker.valueProperty().isNotNull());
        loadDataButton.disableProperty().bind(allFieldsFilled.not());
        
        eventsHistory = FXCollections.observableArrayList();
        
        eventsTable.setItems(eventsHistory);
        dateClmn.setCellValueFactory(new PropertyValueFactory("eventDatetime"));
        magnitudeClmn.setCellValueFactory(new PropertyValueFactory("magnitude"));
        locationClmn.setCellValueFactory(new PropertyValueFactory("location"));
    }    

    @FXML
    private void loadDataAction(ActionEvent event) {
        try {
            eventsHistory.setAll(INGVUtils.readFromINGV(startDatePicker.getValue(), endDatePicker.getValue()));
        } catch (IOException ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("An error occured while retrieving data from INGV APIs. Try again.");
            errorAlert.show();
        }
    }

    @FXML
    private void emptyDataAction(ActionEvent event) {
        eventsHistory.clear();
    }
    
}
