package org.verigo.course_project_client.controllers.teacher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.verigo.course_project_client.MainApplication;

import java.io.IOException;

public class TeacherViewController {
    @FXML
    private Button workspaceBtn;

    @FXML
    private Button groupsBtn;

    @FXML
    public void initialize() {

    }

    @FXML
    public void onCoursesBtnClick(ActionEvent event) {
        loadStage("teacher/courses-view.fxml");
    }

    @FXML
    public void onWorkspaceBtnClick(ActionEvent event) {
        loadStage("teacher/workspace-view.fxml");
    }

    @FXML
    public void onGroupsBtnClick(ActionEvent event) {
        loadStage("teacher/groups-view.fxml");
    }

    @FXML
    public void onCreatingGroupsBtnClick(ActionEvent event) {
        loadStage("teacher/creating-groups-view.fxml");
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
