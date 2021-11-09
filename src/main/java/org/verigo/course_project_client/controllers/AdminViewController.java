package org.verigo.course_project_client.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.verigo.course_project_client.constraints.ROLE;
import org.verigo.course_project_client.models.Role;
import org.verigo.course_project_client.models.UserAdapter;
import org.verigo.course_project_client.models.User;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminViewController {
    //Table and columns
    @FXML
    private TableView usersTable;

    @FXML
    private TableColumn loginColumn;

    @FXML
    private TableColumn surnameColumn;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn createdColumn;

    @FXML
    private TableColumn updatedColumn;

    //Adding a user fields
    @FXML
    private TextField addLoginField;
    private String login;

    @FXML
    private TextField addSurnameField;
    private String surname;

    @FXML
    private TextField addNameField;
    private String name;

    @FXML
    private PasswordField addPasswordField;
    private String password;

    @FXML
    private ChoiceBox addRoleBox;
    private ROLE role;

    @FXML
    private Button addUserButton;

    @FXML
    private void onAddUserButtonClick(ActionEvent actionEvent) {
        login = addLoginField.getText();
        surname = addSurnameField.getText();
        name = addNameField.getText();
        password = this.addPasswordField.getText();

        User user = new User(login, surname, name, new Date(), new Date(), new Role(role), password);

        addUser(user);
    }


    @FXML
    private void initialize() {
        initTableComponent();

        initAddUserComponent();
    }

    private void initTableComponent() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));


        List<User> users = getAllUsers();
        List<UserAdapter> adaptedUsers = new ArrayList<>();

        users.forEach(elem -> {
            adaptedUsers.add(new UserAdapter(elem, elem.getRole().getId()));
        });

        TableColumn<UserAdapter, String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory((new PropertyValueFactory<>("roleName")));

        usersTable.getColumns().addAll(roleColumn);

        usersTable.setItems(FXCollections.observableArrayList(adaptedUsers));
    }

    private void initAddUserComponent() {
        String[] roles = { "Студент", "Учитель", "Администратор" };

        addRoleBox.getSelectionModel().selectedIndexProperty().addListener(
            (observableValue, number, t1) -> {
                if (number.equals(0)) role = ROLE.ADMIN;
                else if (number.equals(1)) role = ROLE.TEACHER;
                else role = ROLE.STUDENT;
            }
        );

        addRoleBox.setItems(FXCollections.observableArrayList(roles));
    }

    private User addUser(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            HttpResponse<JsonNode> apiResponse = Unirest.post("http://localhost:8080/users/")
                    .header("Content-Type", "application/json")
                    .body("{\"login\": \"" + user.getLogin() + "\", \"password\": \""
                            + user.getPassword() + "\", \"surname\": \""
                            + user.getSurname() + "\", \"name\": \""
                            + user.getName() + "\", \"createdAt\": \""
                            + format.format(user.getCreatedAt()) + "\", \"updatedAt\": \""
                            + format.format(user.getUpdatedAt()) + "\", \"role\": "
                            + "{\n" + "\"id\": \"" + user.getRole().getId() + "\"\n}" + "}")
                    .asJson();

            User createdUser = new Gson().fromJson(apiResponse.getBody().toString(), User.class);

            return createdUser;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<User> getAllUsers() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("http://localhost:8080/users/")
                    .header("Content-Type", "application/json")
                    .asJson();

            Type usersListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> res = new Gson().fromJson(apiResponse.getBody().toString(), usersListType);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
