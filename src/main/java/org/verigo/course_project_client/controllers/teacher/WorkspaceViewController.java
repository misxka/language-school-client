package org.verigo.course_project_client.controllers.teacher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
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
import org.verigo.course_project_client.constraints.ROLE;
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

    private final String[] levels = { "A1", "A1+", "A2", "A2+", "B1", "B1+", "B2", "B2+", "C1", "C1+", "C2", "C2+" };
    private final String[] courseFormats = { "Онлайн", "Офлайн" };

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
    private ChoiceBox<String> setCourseLevelBox;
    @FXML
    private TextField setCourseTitleField;
    @FXML
    private TextField setCourseLanguageField;
    @FXML
    private Spinner<Double> setCoursePriceField;
    @FXML
    private ChoiceBox<String> setCourseIsOnlineBox;
    @FXML
    private Button addCourseButton;

    @FXML
    private TextField setLessonTitleField;
    @FXML
    private Button addLessonButton;

    @FXML
    private ChoiceBox<String> setTaskIsHometaskBox;
    @FXML
    private Button addTaskButton;


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

        initAddCourseComponent();
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

    private void initAddCourseComponent() {
        setCourseLevelBox.setItems(FXCollections.observableArrayList(levels));
        setCourseIsOnlineBox.setItems(FXCollections.observableArrayList(courseFormats));

        addCourseButton.setOnAction(event -> {
            if(isCourseValid()) {
                Course course = new Course();
                course.setTitle(setCourseTitleField.getText());
                course.setLanguage(setCourseLanguageField.getText());
                course.setLevel(setCourseLevelBox.getValue());
                if(setCourseIsOnlineBox.getValue().equals("Онлайн"))
                    course.setIsOnline(true);
                else course.setIsOnline(false);
                course.setPrice(new BigDecimal(setCoursePriceField.getEditor().getText().replaceAll(",", ".")));

                Course result = createCourse(course);
                if(result != null) {
                    courses = getAllCourses();
                    observableCourses = FXCollections.observableArrayList(courses);
                    initActions();
                    resetCourseFields();
                }
            }
        });
    }

    private void initAddLessonComponent() {
        addLessonButton.setOnAction(event -> {
            Lesson lesson = new Lesson();
            lesson.setTitle(setLessonTitleField.getText());

            Lesson result = createLesson(lesson);
            if(result != null) {
                lessons = getAllLessons();
                observableLessons = FXCollections.observableArrayList(lessons);
                initActions();
                setLessonTitleField.clear();
            }
        });
    }

    private void resetCourseFields() {
        setCourseTitleField.clear();
        setCourseLanguageField.clear();
        setCoursePriceField.getEditor().setText("50");
    }

    private boolean isCourseValid() {
        if(setCourseTitleField.getText() == null || setCourseTitleField.getText().equals("")) return false;
        if(setCourseLanguageField.getText() == null || setCourseLanguageField.getText().equals("")) return false;
        if(setCourseLevelBox.getValue() == null || setCourseLevelBox.getValue().equals("")) return false;
        if(setCourseIsOnlineBox.getValue() == null || setCourseIsOnlineBox.getValue().equals("")) return false;
        if(setCoursePriceField.getEditor().getText() == null || setCoursePriceField.getEditor().getText().equals("")) return false;
        try {
            Double.parseDouble(setCoursePriceField.getEditor().getText().replaceAll(",", "."));
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
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

    private Course createCourse(Course course) {
        try {
            Unirest.setObjectMapper(new ObjectMapper() {
                com.fasterxml.jackson.databind.ObjectMapper mapper
                        = new com.fasterxml.jackson.databind.ObjectMapper();

                public String writeValue(Object value) {
                    try {
                        return mapper.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return String.valueOf(e);
                    }
                }

                public <T> T readValue(String value, Class<T> valueType) {
                    try {
                        return mapper.readValue(value, valueType);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/courses")
                    .header("Content-Type", "application/json")
                    .body(course)
                    .asJson();

            if(apiResponse.getStatus() == 400) return null;

            Course createdCourse = new Gson().fromJson(apiResponse.getBody().toString(), Course.class);

            return createdCourse;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Lesson createLesson(Lesson lesson) {
        try {
            Unirest.setObjectMapper(new ObjectMapper() {
                com.fasterxml.jackson.databind.ObjectMapper mapper
                        = new com.fasterxml.jackson.databind.ObjectMapper();

                public String writeValue(Object value) {
                    try {
                        return mapper.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return String.valueOf(e);
                    }
                }

                public <T> T readValue(String value, Class<T> valueType) {
                    try {
                        return mapper.readValue(value, valueType);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/lessons")
                    .header("Content-Type", "application/json")
                    .body(lesson)
                    .asJson();

            if(apiResponse.getStatus() == 400) return null;

            Lesson createdLesson = new Gson().fromJson(apiResponse.getBody().toString(), Lesson.class);

            return createdLesson;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }
}
