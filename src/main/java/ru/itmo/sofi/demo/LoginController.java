package ru.itmo.sofi.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.login.CurrentUser;
import ru.itmo.sofi.essence.user.User;
import ru.itmo.sofi.service.UserService;

import java.nio.file.Path;

public class LoginController {
//    private final Path usersFolder = Path.of("data");
    private final Path usersFolder = HelloApplication.DATA_FOLDER;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    private final UserService userService = new UserService();

//    @FXML
//    public void initialize() {
//        userService.loadUsers(usersFolder);
//    }

    @FXML
    private void onLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
//        if (login.isEmpty() || password.isEmpty()) {
//            showError("Введите логин и пароль.");
//            return;
//        }
        try {
//            userService.loadUsers(HelloApplication.DATA_FOLDER);
            User user = userService.login(login, password);
            CurrentUser.login(user);
            closeWindow();
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        if (login.isEmpty() || password.isEmpty()) {
            showError("Введите логин и пароль.");
            return;
        }
        try {
//            userService.loadUsers(HelloApplication.DATA_FOLDER);
            userService.registration(login, password, false);
//            userService.saveUsers(HelloApplication.DATA_FOLDER);
            showInfo("Пользователь зарегистрирован. Теперь можно войти.");
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
        Platform.exit();
    }

    private void closeWindow() {
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(250);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(250);
        alert.showAndWait();
    }
}