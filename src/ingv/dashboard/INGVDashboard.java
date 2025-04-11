package ingv.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author fp
 */
public class INGVDashboard extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DashboardFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        stage.setTitle("INGV Earthquakes Dashboard");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
