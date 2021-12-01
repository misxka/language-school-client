package org.verigo.course_project_client.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.verigo.course_project_client.constraints.ROLE;
import org.verigo.course_project_client.models.Role;
import org.verigo.course_project_client.models.UserAdapter;
import org.verigo.course_project_client.models.User;
import org.verigo.course_project_client.store.DotenvProvider;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

//TODO Update metrics on data change or table reload

public class AdminViewController {
    Dotenv dotenv = DotenvProvider.getInstance().getDotenv();

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
    private Button deleteUserButton;


    //Metrics
    @FXML
    private PieChart usersPieChart;

    @FXML
    private BarChart usersBarChart;


    @FXML
    private void initialize() {
        initTableComponent();

        initAddUserComponent();

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
                deleteUserButton.setDisable(false);
            else
                deleteUserButton.setDisable(true);
        });

        initPieChart();

        initBarChart();
    }

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
    private void handleDelete(ActionEvent actionEvent) {
        UserAdapter selectedUser = (UserAdapter) usersTable.getSelectionModel().getSelectedItem();
        int id = selectedUser.getId();
        adaptedUsers.remove(selectedUser);

        deleteUser(id);

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

        User user = new User(null, login, surname, name, new Date(), new Date(), new Role(role), password, null, null);

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

    private void initTableComponent() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleNameAdapted"));
        roleColumn.setMaxWidth(180);
        roleColumn.setPrefWidth(180);

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

        //TODO Check differences before sending update

        loginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        loginColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setLogin(cellEditEvent.getNewValue());
                adaptedUsers.get(index).setUpdatedAt(new Date());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );

        surnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setSurname(cellEditEvent.getNewValue());
                adaptedUsers.get(index).setUpdatedAt(new Date());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setName(cellEditEvent.getNewValue());
                adaptedUsers.get(index).setUpdatedAt(new Date());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );

        roleColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(roles[0], roles[1], roles[2]));
        roleColumn.setOnEditCommit(
            (EventHandler<TableColumn.CellEditEvent<UserAdapter, String>>) cellEditEvent -> {
                int index = cellEditEvent.getTablePosition().getRow();
                adaptedUsers.get(index).setRoleNameAdapted(cellEditEvent.getNewValue());
                adaptedUsers.get(index).setUpdatedAt(new Date());

                User user = new User(adaptedUsers.get(index));
                updateUser(user);
            }
        );
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

    //API Calls
    private User addUser(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            HttpResponse<JsonNode> apiResponse = Unirest.post(dotenv.get("HOST") + "/users/")
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
            HttpResponse<JsonNode> apiResponse = Unirest.get(dotenv.get("HOST") + "/users/")
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

    private User updateUser(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            HttpResponse<JsonNode> apiResponse = Unirest.put(dotenv.get("HOST") + "/users/" + user.getId())
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

    private String deleteUser(int id) {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.delete(dotenv.get("HOST") + "/users/" + id)
                    .header("Content-Type", "application/json")
                    .asJson();

            return apiResponse.getBody().toString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }


    //Metrics
    private void initPieChart() {

        int adminsAmount = filterByRole(ROLE.ADMIN.ordinal());
        int teachersAmount = filterByRole(ROLE.TEACHER.ordinal());
        int studentsAmount = filterByRole(ROLE.STUDENT.ordinal());

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data(roles[2], adminsAmount),
                        new PieChart.Data(roles[1], teachersAmount),
                        new PieChart.Data(roles[0], studentsAmount));

        this.usersPieChart.setData(pieChartData);
        this.usersPieChart.setLegendSide(Side.BOTTOM);

        usersPieChart.getData().forEach(data -> {
            int amountValue = (int) data.getPieValue();
            String signature;
            if(amountValue == 1) signature = "пользователь";
            else if(amountValue >= 2 && amountValue < 5) signature = "пользователя";
            else signature = "пользователей";
            String amountString = amountValue + " " + signature;
            Tooltip toolTip = new Tooltip(amountString);
            toolTip.setStyle("-fx-font-size: 14");
            Tooltip.install(data.getNode(), toolTip);
        });
    }

    private int filterByRole(int ordinal) {
        return adaptedUsers.stream().filter(user -> user.getRoleName().ordinal() == ordinal).collect(Collectors.toList()).size();
    }

    private void initBarChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        xAxis.setLabel("Роль");
        yAxis.setLabel("Количество");

        String requiredYear = new SimpleDateFormat("yyyy").format(new Date());
        int requiredYearInt = Integer.parseInt(requiredYear);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(String.valueOf(requiredYearInt));
        series1.getData().add(new XYChart.Data(roles[2], filterByYearAndRole(requiredYear, ROLE.ADMIN.ordinal())));
        series1.getData().add(new XYChart.Data(roles[1], filterByYearAndRole(requiredYear, ROLE.TEACHER.ordinal())));
        series1.getData().add(new XYChart.Data(roles[0], filterByYearAndRole(requiredYear, ROLE.STUDENT.ordinal())));

        requiredYear = String.valueOf(--requiredYearInt);
        XYChart.Series series2 = new XYChart.Series();
        series2.setName(requiredYear);
        series2.getData().add(new XYChart.Data(roles[2], filterByYearAndRole(requiredYear, ROLE.ADMIN.ordinal())));
        series2.getData().add(new XYChart.Data(roles[1], filterByYearAndRole(requiredYear, ROLE.TEACHER.ordinal())));
        series2.getData().add(new XYChart.Data(roles[0], filterByYearAndRole(requiredYear, ROLE.STUDENT.ordinal())));


        requiredYear = String.valueOf(--requiredYearInt);
        XYChart.Series series3 = new XYChart.Series();
        series3.setName(requiredYear);
        series3.getData().add(new XYChart.Data(roles[2], filterByYearAndRole(requiredYear, ROLE.ADMIN.ordinal())));
        series3.getData().add(new XYChart.Data(roles[1], filterByYearAndRole(requiredYear, ROLE.TEACHER.ordinal())));
        series3.getData().add(new XYChart.Data(roles[0], filterByYearAndRole(requiredYear, ROLE.STUDENT.ordinal())));

        usersBarChart.getData().addAll(series1, series2, series3);
    }

    private int filterByYearAndRole(String year, int ordinal) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        return adaptedUsers.stream().filter(user -> yearFormat.format(user.getCreatedAt()).equals(year) && user.getRoleName().ordinal() == ordinal).collect(Collectors.toList()).size();
    }
}
