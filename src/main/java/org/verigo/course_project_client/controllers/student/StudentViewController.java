package org.verigo.course_project_client.controllers.student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.verigo.course_project_client.MainApplication;

import java.io.IOException;

public class StudentViewController {
    @FXML
    public void initialize() {

    }

    private void loadStage(String fxml) {
        try {
            Parent root = FXMLLoader.load(MainApplication.class.getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
