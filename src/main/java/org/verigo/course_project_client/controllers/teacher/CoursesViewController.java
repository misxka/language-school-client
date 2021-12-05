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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.store.DotenvProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CoursesViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

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

    @FXML
    private PieChart languagesPieChart;

    @FXML
    private TextField totalCoursesAmount;


    private List<Course> courses = new ArrayList<>();
    private List<Course> filteredCourses = new ArrayList<>();
    private ObservableList<Course> observableCourses;

    private final String[] levels = { "A1", "A1+", "A2", "A2+", "B1", "B1+", "B2", "B2+", "C1", "C1+", "C2", "C2+" };
    private Set<String> languages = new HashSet<>();
    private ObservableList<String> observableLanguages;

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
        courses = getAllCourses();
        observableCourses = FXCollections.observableArrayList(courses);

        initTableComponent();

        initActions();
        addFilterListeners();
    }

    private void initActions() {
        initLanguages();

        fillTable();

        initFilterBox();

        totalCoursesAmount.setText(String.valueOf(courses.size()));
        initPieChart();
    }

    private void applyFilters() {
        filteredCourses = courses.stream().filter(titlePredicate.and(languagePredicate).and(levelPredicate).and(onlinePredicate.or(offlinePredicate)).and(pricePredicate)).collect(Collectors.toList());
        observableCourses = FXCollections.observableArrayList(filteredCourses);
        coursesTable.setItems(observableCourses);
    }

    private void addFilterListeners() {
        titleFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            titleRequested = newValue;
            applyFilters();
        });

        languageFilter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number1, number2) -> {
            try {
                languageRequested = (String) languageFilter.getItems().get((Integer) number2);
            } catch (IndexOutOfBoundsException e) {}
            applyFilters();
        });

        levelFilter.getSelectionModel().selectedIndexProperty().addListener((observableValue, number1, number2) -> {
            try {
                levelRequested = (String) levelFilter.getItems().get((Integer) number2);
            } catch(IndexOutOfBoundsException e) {}
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
        coursesTable.setItems(observableCourses);
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


        TableColumn moreInfoColumn = new TableColumn<>("Подробнее");
        moreInfoColumn.setMaxWidth(100);
        moreInfoColumn.setPrefWidth(100);
        moreInfoColumn.setSortable(false);
        moreInfoColumn.setStyle("-fx-alignment: CENTER");
        moreInfoColumn.setCellFactory((Callback<TableColumn<Course, Boolean>, TableCell<Course, Boolean>>) p -> new ButtonCell());


        coursesTable.getColumns().addAll(moreInfoColumn);
    }

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

    private void initFilterBox() {
        languageFilter.setItems(observableLanguages);
        levelFilter.setItems(FXCollections.observableArrayList(levels));

        BigDecimal max = Collections.max(courses, Comparator.comparing(Course::getPrice)).getPrice();
        BigDecimal min = Collections.min(courses, Comparator.comparing(Course::getPrice)).getPrice();

        priceSlider.setMax(max.doubleValue());
        priceSlider.setMin(min.doubleValue());
        priceSlider.valueProperty().set(max.doubleValue());
        maxPriceOutput.setText(String.valueOf(max.doubleValue()));

        titleRequested = "";
        languageRequested = "";
        levelRequested = "";
        onlineRequested = true;
        offlineRequested = true;
        priceRequested = max;
    }

    private void clearFilters() {
        titleFilter.setText("");
        languageFilter.getSelectionModel().clearSelection();
//        languageFilter.valueProperty().set(null);
        levelFilter.getSelectionModel().clearSelection();
//        levelFilter.valueProperty().set(null);
        onlineFilter.setSelected(true);
        offlineFilter.setSelected(true);
    }

    private class ButtonCell extends TableCell<Course, Boolean> {
        final Button cellButton = new Button("Подробнее");

        ButtonCell(){
            cellButton.setOnAction(t -> {
                Course course = observableCourses.get(getTableRow().getIndex());
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

    private void initLanguages() {
        languages.clear();
        courses.forEach(course -> {
            languages.add(course.getLanguage());
        });
        observableLanguages = FXCollections.observableArrayList(languages);
    }

    private void initPieChart() {
        List<PieChart.Data> chartDataList = new ArrayList<>();
        observableLanguages.forEach(language -> {
            chartDataList.add(new PieChart.Data(language, filterByLanguage(language)));
        });

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(chartDataList);

        languagesPieChart.setData(pieChartData);

        languagesPieChart.getData().forEach(data -> {
            int amountValue = (int) data.getPieValue();
            String signature;
            if(amountValue == 1) signature = "курс";
            else if(amountValue >= 2 && amountValue < 5) signature = "курса";
            else signature = "курсов";
            String amountString = amountValue + " " + signature;
            Tooltip toolTip = new Tooltip(amountString);
            toolTip.setStyle("-fx-font-size: 14");
            Tooltip.install(data.getNode(), toolTip);
        });
    }

    private int filterByLanguage(String language) {
        return courses.stream().filter(course -> course.getLanguage().equals(language)).collect(Collectors.toList()).size();
    }

    @FXML
    private void onRefreshTable(ActionEvent actionEvent) {
        courses = getAllCourses();
        clearFilters();
        observableCourses = FXCollections.observableArrayList(courses);

        initActions();
    }
}


