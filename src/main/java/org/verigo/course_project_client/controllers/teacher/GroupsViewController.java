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
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.models.*;
import org.verigo.course_project_client.store.DotenvProvider;
import org.verigo.course_project_client.store.UserProvider;

import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class GroupsViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

    private User loggedUser = UserProvider.getInstance().getUser();
    private List<Integer> groupsIds = new ArrayList<>();
    private List<CourseGroup> groups = new ArrayList<>();
    private ObservableList<CourseGroup> observableGroups;
    private ObservableList<User> observableParticipants;

    @FXML
    private AnchorPane groupsTableContainer;

    @FXML
    private TableView groupsTable;

    @FXML
    private TableColumn<CourseGroup, String> groupNameColumn;

    @FXML
    private AnchorPane participantsTableContainer;

    @FXML
    private AnchorPane scoresTableContainer;


    @FXML
    public void initialize() {
        groupsIds = getGroupsIds();

        getAllGroups();

        initTableComponent();
        fillTable();
    }

    private void getAllGroups() {
        groupsIds.forEach(id -> {
            groups.add(getGroup(id));
        });
        observableGroups = FXCollections.observableArrayList(groups);
    }

    private List<Integer> getGroupsIds() {
        return loggedUser.getGroups().stream().map(group -> group.getId()).collect(Collectors.toList());
    }

    private CourseGroup getGroup(int groupId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/course-groups/" + groupId)
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

    private void initTableComponent() {
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupNameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");

        TableColumn groupInfoColumn = new TableColumn<>("Подробнее");
        groupInfoColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
        groupInfoColumn.setMaxWidth(150);
        groupInfoColumn.setPrefWidth(150);
        groupInfoColumn.setSortable(false);
        groupInfoColumn.setCellFactory((Callback<TableColumn<CourseGroup, Boolean>, TableCell<CourseGroup, Boolean>>) p -> new ButtonCell());

        groupsTable.getColumns().addAll(groupInfoColumn);
    }

    private void fillTable() {
        groupsTable.setItems(observableGroups);
    }


    private class ButtonCell extends TableCell<CourseGroup, Boolean> {
        final Button cellButton = new Button("Подробнее");

        ButtonCell(){
            cellButton.setGraphic(new FontIcon("fas-chart-line"));
            cellButton.setStyle("-fx-font-size: 14");

            cellButton.setOnAction(t -> {
                participantsTableContainer.getChildren().clear();
                CourseGroup group = observableGroups.get(getTableRow().getIndex());

                Label groupName = new Label();
                groupName.setLayoutX(14);
                groupName.setLayoutY(14);
                groupName.setStyle("-fx-font-size: 20; -fx-text-fill: #46aae8");

                TableView<User> participantsTable = new TableView<>();
                List<User> participants = group.getParticipants();

                Set<Task> availableTasks = new HashSet<>();
                group.getCourse().getLessons().forEach(lesson -> lesson.getTasks().forEach(task -> availableTasks.add((task))));
                List<Integer> availableTaskIds = new ArrayList<>();
                availableTasks.forEach(task -> availableTaskIds.add(task.getId()));

                Button exportBtn = initExportBtn(group, availableTaskIds);
                exportBtn.setLayoutY(14);
                exportBtn.setLayoutX(305);

                participantsTableContainer.getChildren().addAll(groupName, exportBtn);
                groupName.setText(group.getName());

                participants.removeIf(participant -> participant.getId() == loggedUser.getId());
                observableParticipants = FXCollections.observableArrayList(participants);

                initParticipantsTable(participantsTable, observableParticipants);
                participantsTableContainer.getChildren().add(participantsTable);

                participantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                    initScoresTable(newValue, availableTaskIds);
                });
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

    private void initParticipantsTable(TableView table,  ObservableList<User> participants) {
        table.setLayoutX(14);
        table.setLayoutY(54);
        table.setPrefHeight(300);
        table.setPrefWidth(450);

        TableColumn loginColumn = new TableColumn("Логин");
        loginColumn.setPrefWidth(140);
        TableColumn surnameColumn = new TableColumn("Фамилия");
        surnameColumn.setPrefWidth(140);
        TableColumn nameColumn = new TableColumn("Имя");
        nameColumn.setPrefWidth(140);

        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        loginColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
        surnameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
        nameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");

        table.getColumns().addAll(loginColumn, surnameColumn, nameColumn);
        table.setItems(FXCollections.observableArrayList(participants));
    }

    private void initScoresTable(User user, List<Integer> availableIds) {
        Button updateRecordsBtn = new Button("Сохранить изменения");
        updateRecordsBtn.setStyle("-fx-font-size: 14; -fx-background-color: #46aae8; -fx-text-fill: white; -fx-cursor: hand");
        updateRecordsBtn.setLayoutY(110);
        updateRecordsBtn.setLayoutX(802);

        updateRecordsBtn.setOnAction(event -> {
            updateUser(user);
        });

        TableView<User> scoresTable = new TableView<>();
        scoresTable.setLayoutX(14);
        scoresTable.setLayoutY(14);
        scoresTable.setPrefWidth(950);
        scoresTable.setPrefHeight(80);
        scoresTable.setEditable(true);

        TableColumn<User, String> userNameColumn = new TableColumn<>("Студент");
        userNameColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
        userNameColumn.setSortable(false);
        userNameColumn.setCellValueFactory(data -> {
            String userName = data.getValue().getSurname() + " " + data.getValue().getName();
            return new SimpleStringProperty(userName);
        });

        scoresTable.getColumns().add(userNameColumn);

        user.getTasksResults().forEach(result -> {
            if(availableIds.contains(result.getTask().getId())) {
                TableColumn<User, String> column = new TableColumn<>(result.getTask().getTitle());
                column.setPrefWidth(120);
                column.setStyle("-fx-alignment: CENTER; -fx-font-size: 14");
                column.setSortable(false);

                column.setCellFactory(col -> new IntegerEditingCell(1, 10));
                column.setOnEditCommit(event -> {
                    result.setPoints(Integer.parseInt(event.getNewValue()));
                    result.setCompleted(true);
                });

                column.setCellValueFactory(data -> {
                    boolean isCompleted = result.isCompleted();

                    if (!isCompleted) return new SimpleStringProperty("");
                    return new SimpleStringProperty(String.valueOf(result.getPoints()));
                });

                scoresTable.getColumns().add(column);
            }
        });

        scoresTable.setItems(FXCollections.observableArrayList(user));

        scoresTableContainer.getChildren().addAll(scoresTable, updateRecordsBtn);
    }

    public class IntegerEditingCell extends TableCell<User, String> {

        private TextField textField ;
        private TextFormatter<Integer> textFormatter ;

        public IntegerEditingCell(int min, int max) {
            textField = new TextField();
            UnaryOperator<TextFormatter.Change> filter = c -> {
                String newText = c.getControlNewText() ;

                if (newText.isEmpty()) {
                    return c ;
                }

                if (! newText.matches("\\d+")) {
                    return null ;
                }

                int value = Integer.parseInt(newText) ;
                if (value < min || value > max) {
                    return null ;
                } else {
                    return c ;
                }
            };

            StringConverter<Integer> converter = new StringConverter<Integer>() {
                @Override
                public String toString(Integer value) {
                    return value == null ? "" : value.toString() ;
                }

                @Override
                public Integer fromString(String string) {
                    if(string.matches("\\d+")) {
                        return Integer.valueOf(string);
                    } else {
                        return Integer.valueOf(getItem());
                    }
                }
            };

            textFormatter = new TextFormatter<Integer>(converter, 0, filter) ;
            textField.setTextFormatter(textFormatter);

            textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

            textField.setOnAction(e -> commitEdit(textField.getText()));

            textProperty().bind(Bindings
                .when(emptyProperty())
                .then((String)null)
                .otherwise(itemProperty().asString()));

            setGraphic(textField);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        protected void updateItem(String value, boolean empty) {
            super.updateItem(value, empty);
            if (isEditing()) {
                textField.requestFocus();
                textField.selectAll();
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if(getItem() == "") textFormatter.setValue(0);
            else textFormatter.setValue(Integer.valueOf(getItem()));
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.requestFocus();
            textField.selectAll();
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

    }

    private Button initExportBtn(CourseGroup group, List<Integer> availableTaskIds) {
        Button exportBtn = new Button("Создать Excel-файл");
        FontIcon icon = new FontIcon("fas-file-excel");
        icon.setIconColor(Color.WHITE);
        exportBtn.setGraphic(icon);
        exportBtn.setStyle("-fx-font-size: 14; -fx-background-color: #46aae8; -fx-text-fill: white; -fx-cursor: hand");

        exportBtn.setOnAction(event -> {
            exportData(group, availableTaskIds);
        });

        return exportBtn;
    }

    private void exportData(CourseGroup group, List<Integer> availableIds) {
        List<User> participants = group.getParticipants();
        List<UserTaskResult> tasksResults = participants.get(0).getTasksResults();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Результаты - Группа " + group.getName());
        Map<Integer, List<String>> rows = new TreeMap<>();

        List<String> titles = new ArrayList<>();
        titles.add("Студент");
        tasksResults.forEach(result -> {
            if(availableIds.contains(result.getTask().getId())) {
                titles.add(result.getTask().getTitle());
            }
        });
        final int[] rowKeys = {1};
        rows.put(rowKeys[0]++, titles);

        participants.forEach(participant -> {
            List<String> results = new ArrayList<>();
            results.add(participant.getSurname() + " " + participant.getName());
            participant.getTasksResults().forEach(result -> {
                if(availableIds.contains(result.getTask().getId())) {
                    results.add(result.getPoints() == 0 ? "" : String.valueOf(result.getPoints()));
                }
            });
            rows.put(rowKeys[0]++, results);
        });

        Row titlesRow = sheet.createRow(0);
        XSSFFont font= workbook.createFont();
        font.setBold(true);
        CellStyle headerStyle = createStyle(workbook);
        headerStyle.setFont(font);

        CellStyle bodyStyle = createStyle(workbook);

        int cellnum = 0;
        for (int i = 0; i < titles.size(); i++)
        {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i,sheet.getColumnWidth(i) * 30/10);
            org.apache.poi.ss.usermodel.Cell cell = titlesRow.createCell(cellnum++);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(headerStyle);
        }

        int rownum = 1;
        for (int key = 2; key <= rows.size(); key++)
        {
            Row row = sheet.createRow(rownum++);
            List<String> results = rows.get(key);
            int innerCellNum = 0;
            for (String result : results)
            {
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(innerCellNum++);
                cell.setCellValue(result);
                cell.setCellStyle(bodyStyle);
            }
        }

        try {
            FileOutputStream out = new FileOutputStream("Результаты - Группа " + group.getName() + ".xls");
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CellStyle createStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    //API calls
    private User updateUser(User user) {
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

            HttpResponse<JsonNode> apiResponse = Unirest.patch(dotenv.get("HOST") + "/users/" + user.getId())
                    .header("Content-Type", "application/json")
                    .body(user)
                    .asJson();

            if(apiResponse.getStatus() == 400) return null;

            User createdUser = new Gson().fromJson(apiResponse.getBody().toString(), User.class);

            return createdUser;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }
}

//TODO Close studentResults when new Group selected
