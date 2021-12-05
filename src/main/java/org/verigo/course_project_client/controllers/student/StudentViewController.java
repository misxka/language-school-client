package org.verigo.course_project_client.controllers.student;

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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.controllers.teacher.CourseInfoViewController;
import org.verigo.course_project_client.controllers.teacher.CoursesViewController;
import org.verigo.course_project_client.controllers.teacher.GroupsViewController;
import org.verigo.course_project_client.models.*;
import org.verigo.course_project_client.store.DotenvProvider;
import org.verigo.course_project_client.store.UserProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private User loggedUser = UserProvider.getInstance().getUser();

    private List<Course> courses = new ArrayList<>();
    private ObservableList<Course> observableCourses = FXCollections.observableArrayList();

    private Set<Course> myCourses = new HashSet<>();
    private Set<String> myCoursesTitles = new HashSet<>();

    @FXML
    private TableView coursesTable;

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
    private AnchorPane groupsContainer;

    @FXML
    private ListView myCoursesListView;


    @FXML
    public void initialize() {
        courses = getAllCourses();

        initMyCourses();

        setObservableCourses();
        initCoursesTableComponent();
        fillCoursesTable();
        fillMyCoursesListView();
    }

    private void initActions() {
        courses = getAllCourses();

        initMyCourses();

        setObservableCourses();
        fillCoursesTable();
        fillMyCoursesListView();
    }

    private void initMyCourses() {
        myCourses = getMyCourses();
        myCourses.forEach(course -> {
            myCoursesTitles.add(course.getTitle());
        });
    }

    private void setObservableCourses() {
        List<Integer> myCoursesIds = new ArrayList<>();
        myCourses.forEach(course -> myCoursesIds.add(course.getId()));

        List<Course> filteredCourses = new ArrayList<>();
        courses.forEach(course -> {
            if(!myCoursesIds.contains(course.getId())) {
                filteredCourses.add(course);
            }
        });

        observableCourses = FXCollections.observableArrayList(filteredCourses);
    }

    private void fillMyCoursesListView() {
        myCoursesListView.setItems(FXCollections.observableArrayList(myCoursesTitles));
    }

    private void fillCoursesTable() {
        coursesTable.setItems(observableCourses);
    }

    private Set<Course> getMyCourses() {
        Set<Course> myCourses = new HashSet<>();
        loggedUser.getGroups().forEach(group -> {
            myCourses.add(group.getCourse());
        });
        return myCourses;
    }

    private void initCoursesTableComponent() {
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


        TableColumn applyColumn = new TableColumn<>("Записаться");
        applyColumn.setMaxWidth(100);
        applyColumn.setPrefWidth(100);
        applyColumn.setSortable(false);
        applyColumn.setStyle("-fx-alignment: CENTER");
        applyColumn.setCellFactory((Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>>) p -> new ButtonCell());


        coursesTable.getColumns().addAll(applyColumn);
    }

    private class ButtonCell extends TableCell<Course, Boolean> {
        final Button cellButton = new Button("Записаться");
        private CourseGroup selectedGroup = null;

        ButtonCell(){
            cellButton.setOnAction(t -> {
                groupsContainer.getChildren().clear();
                Course course = observableCourses.get(getTableRow().getIndex());

                TableView<CourseGroup> groupsTable = new TableView<>();
                groupsTable.setPrefHeight(240);
                groupsTable.setPrefWidth(200);
                List<CourseGroup> groups = course.getGroups();

                initGroupsTable(groupsTable);
                groupsTable.setItems(FXCollections.observableArrayList(groups));

                Button assignToGroupButton = new Button("Записаться в группу");
                assignToGroupButton.setStyle("-fx-font-size: 14; -fx-background-color: #46aae8; -fx-text-fill: white;");
                assignToGroupButton.setLayoutY(260);
                assignToGroupButton.setDisable(true);

                groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        this.selectedGroup = newSelection;
                        assignToGroupButton.setDisable(false);
                    } else {
                        assignToGroupButton.setDisable(true);
                    }
                });

                assignToGroupButton.setOnAction(event -> {
                    CourseGroup result = assignToGroup(selectedGroup.getId(), loggedUser.getId());
                    if (result != null) {
                        User user = getUserInfo();
                        loggedUser = user != null ? user : loggedUser;

                        initActions();

                        createAlert(Alert.AlertType.INFORMATION, "Успешно", "Вы успешно записаны в группу!");
                    } else createAlert(Alert.AlertType.ERROR, "Ошибка", "Что-то пошло не так...");
                    assignToGroupButton.setDisable(true);
                    selectedGroup = null;
                });

                groupsContainer.getChildren().addAll(groupsTable, assignToGroupButton);
            });

            emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    setGraphic(null);
                } else {
                    setGraphic(cellButton);
                }
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

    private void createAlert(Alert.AlertType type, String title, String contextText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contextText);

        alert.showAndWait();
    }

    private void initGroupsTable(TableView groupsTable) {
        TableColumn<CourseGroup, String> groupNameColumn = new TableColumn<>("Группа");
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupNameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
        groupNameColumn.setPrefWidth(180);

        groupsTable.getColumns().addAll(groupNameColumn);
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

    private CourseGroup assignToGroup(int groupId, int userId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/course-groups/" + groupId + "/user/" + userId)
                    .header("Content-Type", "application/json")
                    .asJson();

            Type groupType = new TypeToken<CourseGroup>(){}.getType();
            CourseGroup res = new Gson().fromJson(apiResponse.getBody().toString(), groupType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUserInfo() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/users/" + loggedUser.getId())
                    .header("Content-Type", "application/json")
                    .asJson();

            Type userType = new TypeToken<User>(){}.getType();
            User res = new Gson().fromJson(apiResponse.getBody().toString(), userType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
