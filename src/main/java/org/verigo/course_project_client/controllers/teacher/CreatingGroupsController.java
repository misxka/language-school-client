package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.CourseGroup;
import org.verigo.course_project_client.models.User;
import org.verigo.course_project_client.store.DotenvProvider;
import org.verigo.course_project_client.store.UserProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CreatingGroupsController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private User loggedUser = UserProvider.getInstance().getUser();

    private List<Course> courses = new ArrayList<>();
    private ObservableList<Course> observableCourses;

    private Course selectedCourse = null;

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn titleColumn;

    @FXML
    private TableColumn languageColumn;

    @FXML
    private TableColumn levelColumn;

    @FXML
    private TableColumn<Course, FontIcon> onlineColumn;

    @FXML
    private TableColumn priceColumn;

    @FXML
    private Button createGroupButton;

    @FXML
    private TextField groupNameField;

    @FXML
    private AnchorPane addGroupContainer;

    @FXML
    private Label wrongInputLabel;


    @FXML
    public void initialize() {
        courses = getAllCourses();
        observableCourses = FXCollections.observableArrayList(courses);

        initTableComponent();

        initAddGroupContainer();

        fillTable();
    }

    private void fillTable() {
        coursesTable.setItems(observableCourses);
    }

    private void initAddGroupContainer() {
        wrongInputLabel = new Label("Должно содержать минимум 4 символа.");
        wrongInputLabel.setLayoutY(69);
        wrongInputLabel.setLayoutX(14);
        wrongInputLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10;");
        wrongInputLabel.setVisible(false);
        addGroupContainer.getChildren().add(wrongInputLabel);

        createGroupButton.setOnAction(event -> {
            if(!groupNameField.getText().matches("^.{4,}$") || groupNameField.getText() == null) {
                wrongInputLabel.setVisible(true);
                return;
            }

            wrongInputLabel.setVisible(false);
            CourseGroup createdGroup = addGroupToCourse(selectedCourse.getId(), groupNameField.getText());

            if(createdGroup != null) {
                CourseGroup groupWithUser = addUserToGroup(createdGroup.getId(), loggedUser.getId());

                createAlert(Alert.AlertType.INFORMATION, "Успешно", "Группа успешно добавлена к курсу! Вы записаны в качестве преподавателя.");
            } else {
                createAlert(Alert.AlertType.ERROR, "Ошибка", "Что-то пошло не так... Группа не была создана.");
            }

            coursesTable.getSelectionModel().clearSelection();
            selectedCourse = null;

            groupNameField.setText(null);
            groupNameField.setDisable(true);
            createGroupButton.setDisable(true);
        });
    }

    private void initTableComponent() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelColumn.setStyle("-fx-alignment: CENTER");
        onlineColumn.setCellValueFactory(data -> {
            boolean value = data.getValue().getIsOnline();
            FontIcon trueIcon = new FontIcon("fas-check");
            trueIcon.setIconColor(Color.GREEN);
            trueIcon.setIconSize(14);

            FontIcon falseIcon = new FontIcon("fas-times");
            falseIcon.setIconColor(Color.RED);
            falseIcon.setIconSize(14);

            return (value == true ? new SimpleObjectProperty<>(trueIcon) : new SimpleObjectProperty<>(falseIcon));
        });
        onlineColumn.setStyle("-fx-alignment: CENTER");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setStyle("-fx-alignment: CENTER");

        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCourse = newSelection;
            createGroupButton.setDisable(false);
            groupNameField.setDisable(false);
        });
    }

    public void onRefreshTable(ActionEvent actionEvent) {
        courses = getAllCourses();
        observableCourses = FXCollections.observableArrayList(courses);

        fillTable();
    }

    private void createAlert(Alert.AlertType type, String title, String contextText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contextText);

        alert.showAndWait();
    }

    //API Calls
    private ArrayList<Course> getAllCourses() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/courses/")
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

    private CourseGroup addGroupToCourse(int courseId, String groupName) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/course-groups/course/" + courseId)
                    .header("Content-Type", "application/json")
                    .body("{\"name\": \"" + groupName + "\"}")
                    .asJson();

            Type courseGroupType = new TypeToken<CourseGroup>(){}.getType();
            CourseGroup res = new Gson().fromJson(apiResponse.getBody().toString(), courseGroupType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CourseGroup addUserToGroup(int groupId, int userId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/course-groups/" + groupId + "/user/" + userId)
                    .header("Content-Type", "application/json")
                    .asJson();

            Type courseGroupType = new TypeToken<CourseGroup>(){}.getType();
            CourseGroup res = new Gson().fromJson(apiResponse.getBody().toString(), courseGroupType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
