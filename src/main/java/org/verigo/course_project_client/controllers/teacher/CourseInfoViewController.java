package org.verigo.course_project_client.controllers.teacher;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.verigo.course_project_client.models.Course;

public class CourseInfoViewController {
    private Course course;


    public void setCourse(Course course) {
        this.course = course;
    }

    @FXML
    public void initialize() {

    }
}
