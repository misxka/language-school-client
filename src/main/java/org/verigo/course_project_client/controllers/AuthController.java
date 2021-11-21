package org.verigo.course_project_client.controllers;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.verigo.course_project_client.MainApplication;
import org.verigo.course_project_client.constraints.ROLE;
import org.verigo.course_project_client.models.AuthResponse;

import java.io.IOException;


public class AuthController {
    private static final String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+){8,}$";
    private static final String minRequirement = "Минимум 8 символов, 1 буква и 1 цифра";
    private static final String wrongCredentials = "Данные неверны. Попробуйте ещё раз.";

    private String login;
    private String password;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMsg;

    @FXML
    private Label passwordMsg;

    @FXML
    private void initialize() {
        loginField.setPromptText("Введите Ваш логин...");
        passwordField.setPromptText("Введите Ваш пароль...");
    }

    public void onLoginBtnClick(ActionEvent actionEvent) {
        if(!loginField.getText().matches(pattern)) {
            loginField.setStyle("-fx-border-color: red");
            loginMsg.setText(minRequirement);
            return;
        }

        if(!passwordField.getText().matches(pattern)) {
            passwordField.setStyle("-fx-border-color: red");
            passwordMsg.setText(minRequirement);
            return;
        }

        this.login = loginField.getText();
        this.password = passwordField.getText();
        AuthResponse response = handleLogin();

        if(!response.getMessage().equals("Success")) {
            loginField.setStyle("-fx-border-color: red");
            passwordField.setStyle("-fx-border-color: red");
            passwordMsg.setText(wrongCredentials);
            return;
        }

        if(response.getRole() == ROLE.ADMIN)
            openWindow();
    }

    private AuthResponse handleLogin() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.post("http://localhost:8080/auth/")
                    .header("Content-Type", "application/json")
                    .body("{\"login\": \"" + this.login + "\", \"password\": \"" + this.password + "\"}")
                    .asJson();

            AuthResponse res = new Gson().fromJson(apiResponse.getBody().toString(), AuthResponse.class);

            return res;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openWindow() {
        Stage stage = (Stage) this.loginField.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("admin-view.fxml"));
        stage.setTitle("Admin");
        try {
            stage.setScene(new Scene(fxmlLoader.load(), 1200, 750));
            stage.setX(200);
            stage.setY(20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}