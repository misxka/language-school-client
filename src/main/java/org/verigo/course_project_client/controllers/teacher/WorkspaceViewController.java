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
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.Lesson;
import org.verigo.course_project_client.models.Task;
import org.verigo.course_project_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private Course selectedCourse = null;
    private Lesson selectedLesson = null;
    private Task selectedTask = null;


    private List<Course> courses = new ArrayList<>();
    private ObservableList<Course> observableCourses;

    private List<Lesson> lessons = new ArrayList<>();
    private ObservableList<Lesson> observableLessons;

    private List<Task> tasks = new ArrayList<>();
    private ObservableList<Task> observableTasks;


    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, String> coursesTitleColumn;
    @FXML
    private TableColumn<Course, String> coursesLanguageColumn;
    @FXML
    private TableColumn<Course, String> coursesLevelColumn;
    @FXML
    private TableColumn<Course, FontIcon> coursesIsOnlineColumn;
    @FXML
    private TableColumn<Course, BigDecimal> coursesPriceColumn;


    @FXML
    private TableView<Lesson> lessonsTable;
    @FXML
    private TableColumn<Lesson, String> lessonsTitleColumn;

    @FXML
    private TableView<Task> tasksTable;
    @FXML
    private TableColumn<Task, String> tasksTitleColumn;
    @FXML
    private TableColumn<Task, String> tasksDescriptionColumn;
    @FXML
    private TableColumn<Task, FontIcon> tasksIsHometaskColumn;
    @FXML
    private TableColumn<Task, Integer> tasksMaxPointsColumn;

    @FXML
    private TextField addToCourseField;
    @FXML
    private TextField addToLessonField;
    @FXML
    private TextField addLessonToCourseField;
    @FXML
    private TextField addTaskToLessonField;
    @FXML
    private Button switchCourseLessonButton;
    @FXML
    private Button switchLessonTaskButton;


    @FXML
    public void initialize() {
        this.courses = getAllCourses();
        observableCourses = FXCollections.observableArrayList(courses);
        this.lessons = getAllLessons();
        observableLessons = FXCollections.observableArrayList(lessons);
        this.tasks = getAllTasks();
        observableTasks = FXCollections.observableArrayList(tasks);

        initCoursesTable();
        initLessonsTable();
        initTasksTable();

        initSwitchButtons();

        initActions();
    }

    private void initActions() {
        coursesTable.setItems(observableCourses);
        lessonsTable.setItems(observableLessons);
        tasksTable.setItems(observableTasks);
    }

    private void initCoursesTable() {
        coursesTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        coursesLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        coursesLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        coursesLevelColumn.setStyle("-fx-alignment: CENTER");
        coursesIsOnlineColumn.setCellValueFactory(new PropertyValueFactory<>("isOnline"));
        coursesIsOnlineColumn.setCellValueFactory(data -> {
            boolean value = data.getValue().getIsOnline();
            FontIcon trueIcon = new FontIcon("fas-check");
            trueIcon.setIconColor(Color.GREEN);
            trueIcon.setIconSize(14);

            FontIcon falseIcon = new FontIcon("fas-times");
            falseIcon.setIconColor(Color.RED);
            falseIcon.setIconSize(14);

            return (value == true ? new SimpleObjectProperty<>(trueIcon) : new SimpleObjectProperty<>(falseIcon));
        });
        coursesIsOnlineColumn.setStyle("-fx-alignment: CENTER");
        coursesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        coursesPriceColumn.setStyle("-fx-alignment: CENTER");

        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.selectedCourse = newSelection;
                addToCourseField.setText(newSelection.getTitle());

                if(selectedCourse != null && selectedLesson != null) switchCourseLessonButton.setDisable(false);
            }
        });
    }

    private void initLessonsTable() {
        lessonsTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        lessonsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.selectedLesson = newSelection;
                addToLessonField.setText(newSelection.getTitle());
                addLessonToCourseField.setText(newSelection.getTitle());

                if(selectedCourse != null && selectedLesson != null) switchCourseLessonButton.setDisable(false);
                if(selectedTask != null && selectedLesson != null) switchLessonTaskButton.setDisable(false);
            }
        });
    }

    private void initTasksTable() {
        tasksTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        tasksDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        tasksIsHometaskColumn.setCellValueFactory(new PropertyValueFactory<>("isHometask"));
        tasksIsHometaskColumn.setCellValueFactory(data -> {
            boolean value = data.getValue().getIsHometask();
            FontIcon trueIcon = new FontIcon("fas-check");
            trueIcon.setIconColor(Color.GREEN);
            trueIcon.setIconSize(14);

            FontIcon falseIcon = new FontIcon("fas-times");
            falseIcon.setIconColor(Color.RED);
            falseIcon.setIconSize(14);

            return (value == true ? new SimpleObjectProperty<>(trueIcon) : new SimpleObjectProperty<>(falseIcon));
        });
        tasksIsHometaskColumn.setStyle("-fx-alignment: CENTER");
        tasksMaxPointsColumn.setCellValueFactory(new PropertyValueFactory<>("maxPoints"));
        tasksMaxPointsColumn.setStyle("-fx-alignment: CENTER");

        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.selectedTask = newSelection;
                addTaskToLessonField.setText(newSelection.getTitle());

                if(selectedTask != null && selectedLesson != null) switchLessonTaskButton.setDisable(false);
            }
        });
    }

    @FXML
    private void onRefreshCoursesTable(ActionEvent actionEvent) {
        courses = getAllCourses();
        observableCourses = FXCollections.observableArrayList(courses);

        initActions();
    }

    @FXML
    private void onRefreshLessonsTable(ActionEvent actionEvent) {
        lessons = getAllLessons();
        observableLessons = FXCollections.observableArrayList(lessons);

        initActions();
    }

    @FXML
    private void onRefreshTasksTable(ActionEvent actionEvent) {
        tasks = getAllTasks();
        observableTasks = FXCollections.observableArrayList(tasks);

        initActions();
    }

    private void initSwitchButtons() {
        switchCourseLessonButton.setOnAction(event -> {
            Lesson result = addLessonToCourse(selectedLesson.getId(), selectedCourse.getId());
            if(result == null) {
                createAlert(Alert.AlertType.ERROR, "Ошибка", "Увы(: Не получилось добавить урок.");
            } else {
                createAlert(Alert.AlertType.INFORMATION, "Успешно", "Урок успешно добавлен в курс!");
            }
        });

        switchLessonTaskButton.setOnAction(event -> {
            Task result = addTaskToLesson(selectedTask.getId(), selectedLesson.getId());
            if(result == null) {
                createAlert(Alert.AlertType.ERROR, "Ошибка", "Увы(: Не получилось добавить задание.");
            } else {
                createAlert(Alert.AlertType.INFORMATION, "Успешно", "Задание успешно добавлено к уроку!");
            }
        });
    }

    private void createAlert(Alert.AlertType type, String title, String contextText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contextText);

        alert.showAndWait();
    }

    //API calls
    private List<Course> getAllCourses() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/courses/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type listType = new TypeToken<ArrayList<Course>>(){}.getType();
            ArrayList<Course> res = new Gson().fromJson(apiResponse.getBody().toString(), listType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Lesson> getAllLessons() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/lessons/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type listType = new TypeToken<ArrayList<Lesson>>(){}.getType();
            ArrayList<Lesson> res = new Gson().fromJson(apiResponse.getBody().toString(), listType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Task> getAllTasks() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/tasks/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
            ArrayList<Task> res = new Gson().fromJson(apiResponse.getBody().toString(), listType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Lesson addLessonToCourse(int lessonId, int courseId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/lessons/" + lessonId + "/course/" + courseId)
                    .header("Content-Type", "application/json")
                    .asJson();

            Type lessonType = new TypeToken<Lesson>(){}.getType();
            Lesson res = new Gson().fromJson(apiResponse.getBody().toString(), lessonType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task addTaskToLesson(int taskId, int lessonId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/tasks/" + taskId + "/lesson/" + lessonId)
                    .header("Content-Type", "application/json")
                    .asJson();

            Type taskType = new TypeToken<Task>(){}.getType();
            Task res = new Gson().fromJson(apiResponse.getBody().toString(), taskType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
