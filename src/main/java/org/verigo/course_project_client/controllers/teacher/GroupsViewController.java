package org.verigo.course_project_client.controllers.teacher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.models.Course;
import org.verigo.course_project_client.models.CourseGroup;
import org.verigo.course_project_client.models.User;
import org.verigo.course_project_client.store.UserProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupsViewController {
    private User loggedUser = UserProvider.getInstance().getUser();
    private List<Integer> groupsIds = new ArrayList<>();
    private List<CourseGroup> groups = new ArrayList<>();

    @FXML
    private AnchorPane groupsTableContainer;

    @FXML
    private TableView groupsTable;

    @FXML
    private TableColumn<CourseGroup, String> groupNameColumn;

    @FXML
    private AnchorPane participantsTableContainer;


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
    }

    private List<Integer> getGroupsIds() {
        return loggedUser.getGroups().stream().map(group -> group.getId()).collect(Collectors.toList());
    }

    private CourseGroup getGroup(int groupId) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("http://localhost:8080/course-groups/" + groupId)
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
        groupInfoColumn.setMaxWidth(150);
        groupInfoColumn.setPrefWidth(150);
        groupInfoColumn.setSortable(false);
        groupInfoColumn.setStyle("-fx-alignment: CENTER");
        groupInfoColumn.setCellFactory((Callback<TableColumn<CourseGroup, Boolean>, TableCell<CourseGroup, Boolean>>) p -> new ButtonCell());

        groupsTable.getColumns().addAll(groupInfoColumn);
    }

    private void fillTable() {
        groupsTable.setItems(FXCollections.observableArrayList(groups));
    }


    private class ButtonCell extends TableCell<CourseGroup, Boolean> {
        final Button cellButton = new Button("Подробнее");

        ButtonCell(){
            cellButton.setGraphic(new FontIcon("fas-chart-line"));
            cellButton.setStyle("-fx-font-size: 14");

            cellButton.setOnAction(t -> {
                CourseGroup group = groups.get(getTableRow().getIndex());
                int id = group.getId();

                //TODO Here add new table that contains info about participants
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
