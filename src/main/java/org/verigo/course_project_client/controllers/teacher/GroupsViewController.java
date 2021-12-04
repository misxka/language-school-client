package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.models.*;
import org.verigo.course_project_client.store.DotenvProvider;
import org.verigo.course_project_client.store.UserProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

                participantsTableContainer.getChildren().add(groupName);
                groupName.setText(group.getName());

                TableView<User> participantsTable = new TableView<>();
                Set<User> participants = group.getParticipants();

                Set<Task> availableTasks = new HashSet<>();
                group.getCourse().getLessons().forEach(lesson -> lesson.getTasks().forEach(task -> availableTasks.add((task))));
                List<Integer> availableTaskIds = new ArrayList<>();
                availableTasks.forEach(task -> availableTaskIds.add(task.getId()));

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
        TableView<User> scoresTable = new TableView<>();
        scoresTable.setLayoutX(14);
        scoresTable.setLayoutY(14);
        scoresTable.setPrefWidth(950);
        scoresTable.setPrefHeight(80);

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

                column.setCellValueFactory(data -> {
                    boolean isCompleted = result.isCompleted();

                    if (!isCompleted) return new SimpleStringProperty("");
                    return new SimpleStringProperty(String.valueOf(result.getPoints()));
                });

                scoresTable.getColumns().add(column);
            }
        });

        scoresTable.setItems(FXCollections.observableArrayList(user));

        scoresTableContainer.getChildren().add(scoresTable);
    }
}
