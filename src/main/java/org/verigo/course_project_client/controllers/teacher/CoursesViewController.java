package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.models.Course;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CoursesViewController {
    private final String[] levels = { "A1", "A1+", "A2", "A2+", "B1", "B1+", "B2", "B2+", "C1", "C1+", "C2", "C2+" };
    private final String[] languages = { "Английский", "Французский", "Немецкий" };

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


    @FXML
    private Slider priceSlider;

    @FXML
    private ChoiceBox languageFilter;

    @FXML
    private ChoiceBox levelFilter;

    @FXML
    private TextField titleFilter;

    @FXML
    private CheckBox onlineFilter;

    @FXML
    private CheckBox offlineFilter;

    @FXML
    private TextField maxPriceOutput;


    private List<Course> courses = new ArrayList<>();
    private List<Course> filteredCourses = new ArrayList<>();


    private String titleRequested = "";
    private String languageRequested = "";
    private String levelRequested = "";
    private boolean onlineRequested = true;
    private boolean offlineRequested = true;
    private BigDecimal priceRequested;

    private Predicate<Course> titlePredicate = course -> course.getTitle().toLowerCase(Locale.ROOT).contains(titleRequested.toLowerCase(Locale.ROOT));
    private Predicate<Course> languagePredicate = course -> course.getLanguage().toLowerCase(Locale.ROOT).contains(languageRequested.toLowerCase(Locale.ROOT));
    private Predicate<Course> levelPredicate = course -> levelRequested.equals("") ? true : course.getLevel().toLowerCase(Locale.ROOT).equals(levelRequested.toLowerCase(Locale.ROOT));
    private Predicate<Course> onlinePredicate = course -> course.getIsOnline() == onlineRequested;
    private Predicate<Course> offlinePredicate = course -> course.getIsOnline() != offlineRequested;
    private Predicate<Course> pricePredicate = course -> course.getPrice().compareTo(priceRequested) <= 0;


    @FXML
    public void initialize() {
        initTableComponent();
        initFilterBox();

        addFilterListeners();
    }

    private void applyFilters() {
        filteredCourses = courses.stream().filter(titlePredicate.and(languagePredicate).and(levelPredicate).and(onlinePredicate.or(offlinePredicate)).and(pricePredicate)).collect(Collectors.toList());
        coursesTable.setItems(FXCollections.observableArrayList(filteredCourses));
    }

    private void addFilterListeners() {
        titleFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            titleRequested = newValue;
            applyFilters();
        });

        languageFilter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number1, number2) -> {
            languageRequested = (String) languageFilter.getItems().get((Integer) number2);
            applyFilters();
        });

        levelFilter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number1, number2) -> {
            levelRequested = (String) levelFilter.getItems().get((Integer) number2);
            applyFilters();
        });

        onlineFilter.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            onlineRequested = newValue;
            applyFilters();
        });

        offlineFilter.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            offlineRequested = newValue;
            applyFilters();
        });

        priceSlider.setOnMouseReleased(event -> {
            priceRequested = BigDecimal.valueOf(priceSlider.getValue());
            applyFilters();
        });

        priceSlider.setOnMouseDragged(event -> {
            maxPriceOutput.setText(String.valueOf(new BigDecimal(priceSlider.getValue()).setScale(2, RoundingMode.HALF_EVEN)));
        });
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


        TableColumn moreInfoColumn = new TableColumn<>("Подробнее");
        moreInfoColumn.setMaxWidth(100);
        moreInfoColumn.setPrefWidth(100);
        moreInfoColumn.setSortable(false);
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

    private void initFilterBox() {
        languageFilter.setItems(FXCollections.observableArrayList(languages));
        levelFilter.setItems(FXCollections.observableArrayList(levels));

        BigDecimal max = Collections.max(courses, Comparator.comparing(Course::getPrice)).getPrice();
        BigDecimal min = Collections.min(courses, Comparator.comparing(Course::getPrice)).getPrice();

        priceSlider.setMax(max.doubleValue());
        priceSlider.setMin(min.doubleValue());

        priceRequested = max;
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
}


