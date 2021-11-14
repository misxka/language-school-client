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
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
    private static final String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+){8,}$";
    private static final String namePattern = "^([а-яА-Я]){2,}$";
    private static final String minRequirement = "Минимум 8 символов, 1 буква и 1 цифра";
    private static final String nameRequirement = "Минимум 2 кириллических буквы";
    private static final String wrongCredentials = "Пользователь с таким логином уже существует.";
    private static final String pleaseSelectRole = "Пожалуйста, выберите роль";

    private String[] roles = { "Студент", "Учитель", "Администратор" };

    private List<UserAdapter> adaptedUsers = new ArrayList<>();


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

    TableColumn<UserAdapter, String> roleColumn = new TableColumn<>("Роль");

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
    private Label loginMsg;
    @FXML
    private Label passwordMsg;
    @FXML
    private Label surnameMsg;
    @FXML
    private Label nameMsg;
    @FXML
    private Label roleMsg;

    @FXML
    private void onRefreshTable(ActionEvent actionEvent) {
        List<User> users = getAllUsers();

        adaptedUsers.clear();
        users.forEach(elem -> {
            adaptedUsers.add(new UserAdapter(elem, elem.getRole().getId()));
        });

        fillTable();
    }


    @FXML
    private void onAddUserButtonClick(ActionEvent actionEvent) {
        resetWarnings();
        if(beforeSendValidateInput() != 0) return;

        login = addLoginField.getText();
        surname = addSurnameField.getText();
        name = addNameField.getText();
        password = this.addPasswordField.getText();

        User user = new User(null, login, surname, name, new Date(), new Date(), new Role(role), password);

        User createdUser = addUser(user);

        if(afterSendValidateInput(createdUser) != 0) return;

        adaptedUsers.add(new UserAdapter(createdUser, createdUser.getRole().getId()));
        fillTable();
    }

    private void resetWarnings() {
        addLoginField.setStyle("");
        addSurnameField.setStyle("");
        addNameField.setStyle("");
        addPasswordField.setStyle("");
        addRoleBox.setStyle("");

        loginMsg.setText("");
        surnameMsg.setText("");
        nameMsg.setText("");
        passwordMsg.setText("");
        roleMsg.setText("");
    }

    private int beforeSendValidateInput() {
        if(!addLoginField.getText().matches(pattern)) {
            addLoginField.setStyle("-fx-border-color: red");
            loginMsg.setText(minRequirement);
            return 1;
        }

        if(!addSurnameField.getText().matches(namePattern)) {
            addSurnameField.setStyle("-fx-border-color: red");
            surnameMsg.setText(nameRequirement);
            return 1;
        }

        if(!addNameField.getText().matches(namePattern)) {
            addNameField.setStyle("-fx-border-color: red");
            nameMsg.setText(nameRequirement);
            return 1;
        }

        if(!addPasswordField.getText().matches(pattern)) {
            addPasswordField.setStyle("-fx-border-color: red");
            passwordMsg.setText(minRequirement);
            return 1;
        }

        if(addRoleBox.getSelectionModel().isEmpty()) {
            addRoleBox.setStyle("-fx-border-color: red");
            roleMsg.setText(pleaseSelectRole);
            return 1;
        }

        return 0;
    }

    private int afterSendValidateInput(User createdUser) {
        if(createdUser == null) {
            addLoginField.setStyle("-fx-border-color: red");
            loginMsg.setText(wrongCredentials);
            return 1;
        }
        return 0;
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

        roleColumn.setCellValueFactory((new PropertyValueFactory<>("roleNameAdapted")));

        usersTable.getColumns().addAll(roleColumn);

        List<User> users = getAllUsers();

        users.forEach(elem -> {
            adaptedUsers.add(new UserAdapter(elem, elem.getRole().getId()));
        });

        setTableEditing();

        fillTable();
    }

    private void setTableEditing() {
        usersTable.setEditable(true);

        loginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roleColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(roles[0], roles[1], roles[2]));
    }

    private void fillTable() {
        usersTable.setItems(FXCollections.observableArrayList(adaptedUsers));
    }

    private void initAddUserComponent() {
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

            if(apiResponse.getStatus() == 400) return null;

            User createdUser = new Gson().fromJson(apiResponse.getBody().toString(), User.class);

            return createdUser;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
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
