package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.models.Course;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceViewController {
    @FXML
    private TableView coursesTable;

    @FXML
    private TableColumn titleColumn;

    @FXML
    private TableColumn languageColumn;

    @FXML
    private TableColumn levelColumn;

    @FXML
    private TableColumn onlineColumn;

    @FXML
    private TableColumn priceColumn;


    private List<Course> courses = new ArrayList<>();



    @FXML
    public void initialize() {
        initTableComponent();
    }

    private void fillTable() {
        coursesTable.setItems(FXCollections.observableArrayList(courses));
    }

    private void initTableComponent() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        onlineColumn.setCellValueFactory(new PropertyValueFactory<>("isOnline"));
        onlineColumn.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty) ;
                setText(empty ? null : item ? "да" : "нет" );
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));


        TableColumn moreInfoColumn = new TableColumn<>("moreInfo");
        moreInfoColumn.setMaxWidth(100);
        moreInfoColumn.setPrefWidth(100);
        moreInfoColumn.setSortable(false);
        moreInfoColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Course, Boolean>, ObservableValue<Boolean>>) p ->
            new SimpleBooleanProperty(p.getValue() != null));
        moreInfoColumn.setCellFactory((Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>>) p ->
            new ButtonCell());


        coursesTable.getColumns().addAll(moreInfoColumn);

        courses = getAllCourses();

        //TODO setTableEditing();

        fillTable();
    }

    private ArrayList<Course> getAllCourses() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("http://localhost:8080/courses/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type coursesListType = new TypeToken<ArrayList<Course>>(){}.getType();
            ArrayList<Course> res = new Gson().fromJson(apiResponse.getBody().toString(), coursesListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }



    private class ButtonCell extends TableCell<Course, Boolean> {
        final Button cellButton = new Button("Подробнее");

        ButtonCell(){
            cellButton.setOnAction(t -> {
                Course course = courses.get(getTableRow().getIndex());
                int id = course.getId();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("teacher/course-info-view.fxml"));
                CourseInfoViewController courseInfoViewController = new CourseInfoViewController();
                courseInfoViewController.setCourse(course);
                fxmlLoader.setController(courseInfoViewController);

                stage.setTitle("Курс \"" + course.getTitle() + "\"");
                try {
                    stage.setScene(new Scene(fxmlLoader.load(), 1000, 650));
                    stage.setX(300);
                    stage.setY(70);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.show();
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }
}


