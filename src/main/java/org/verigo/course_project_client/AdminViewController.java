package org.verigo.course_project_client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.verigo.course_project_client.models.UsersAdapter;
import org.verigo.course_project_client.models.UsersResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminViewController {
    @FXML
    private TableView usersTable;

    @FXML
    private TableColumn loginColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private TableColumn nameColumn;

//    @FXML
//    private TableColumn roleColumn;

    @FXML
    private TableColumn createdColumn;

    @FXML
    private TableColumn updatedColumn;

    @FXML
    private void initialize() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));


        List<UsersResponse> users = getAllUsers();
        List<UsersAdapter> adaptedUsers = new ArrayList<>();

        users.forEach(elem -> {
            adaptedUsers.add(new UsersAdapter(elem, elem.getRole().getId()));
        });

        TableColumn<UsersAdapter, String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory((new PropertyValueFactory<>("roleName")));

        usersTable.getColumns().addAll(roleColumn);

        usersTable.setItems(FXCollections.observableArrayList(adaptedUsers));
    }

    private ArrayList<UsersResponse> getAllUsers() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("http://localhost:8080/users/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type usersListType = new TypeToken<ArrayList<UsersResponse>>(){}.getType();
            ArrayList<UsersResponse> res = new Gson().fromJson(apiResponse.getBody().toString(), usersListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
