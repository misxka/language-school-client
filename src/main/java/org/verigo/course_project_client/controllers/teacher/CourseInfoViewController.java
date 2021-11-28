package org.verigo.course_project_client.controllers.teacher;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.Task;

public class CourseInfoViewController {
    private Course course;

    @FXML
    private Label courseInfoLabel;

    @FXML
    private AnchorPane treeTableViewContainer;


    TreeTableView<Task> treeTableView = new TreeTableView<>();


    public void setCourse(Course course) {
        this.course = course;
    }

    @FXML
    public void initialize() {
        courseInfoLabel.setText("Структура курса " + course.getTitle());

        initTreeTableView();
        fillTreeTableView();
    }

    private void initTreeTableView() {
        treeTableView.setLayoutX(0);
        treeTableView.setLayoutY(0);

        TreeTableColumn<Task, String> titleColumn = new TreeTableColumn<>("Название");
        titleColumn.setPrefWidth(150);
        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Описание");
        descriptionColumn.setPrefWidth(250);
        TreeTableColumn<Task, FontIcon> hometaskColumn = new TreeTableColumn<>("Домашнее задание?");
        hometaskColumn.setPrefWidth(140);
        hometaskColumn.setStyle("-fx-alignment: CENTER");
        TreeTableColumn<Task, String> maxPointsColumn = new TreeTableColumn<>("Макс. оценка");
        maxPointsColumn.setPrefWidth(100);
        maxPointsColumn.setStyle("-fx-alignment: CENTER");

        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        hometaskColumn.setCellValueFactory(data -> {
            String value = data.getValue().getValue().getIsHometaskString();
            FontIcon trueIcon = new FontIcon("fas-check");
            trueIcon.setIconColor(Color.GREEN);
            trueIcon.setIconSize(14);

            FontIcon falseIcon = new FontIcon("fas-times");
            falseIcon.setIconColor(Color.RED);
            falseIcon.setIconSize(14);

            return (value.equals("") ? new SimpleObjectProperty<>(new FontIcon()) : (value.equals("true") ? new SimpleObjectProperty<>(trueIcon) : new SimpleObjectProperty<>(falseIcon)));
        });
        maxPointsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("maxPointsString"));

        treeTableView.getColumns().add(titleColumn);
        treeTableView.getColumns().add(descriptionColumn);
        treeTableView.getColumns().add(hometaskColumn);
        treeTableView.getColumns().add(maxPointsColumn);

        treeTableViewContainer.getChildren().add(treeTableView);
    }

    private void fillTreeTableView() {
        TreeItem lessons = new TreeItem(new Task("Уроки"));
        lessons.setExpanded(true);
        course.getLessons().forEach(lesson -> {
            TreeItem lessonItem = new TreeItem(new Task(lesson.getTitle()));
            lessonItem.setExpanded(true);
            lesson.getTasks().forEach(task -> {
                TreeItem taskItem = new TreeItem(new Task(task));
                lessonItem.getChildren().add(taskItem);
            });
            lessons.getChildren().add(lessonItem);
        });
        treeTableView.setRoot(lessons);
    }
}
