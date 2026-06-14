package ru.itmo.sofi.demo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.itmo.sofi.essence.user.User;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.UserService;
import ru.itmo.sofi.login.CurrentUser;

import java.util.Optional;

public class UserController {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, Boolean> adminColumn;
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        userTable.setItems(FXCollections.observableArrayList(userService.getAll()));
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onResetPassword() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите пользователя.");
            return;
        }
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Новый пароль");
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.setTitle("Сброс пароля");
        dialog.setHeaderText("Новый пароль для пользователя: " + selected.getLogin());
        dialog.getDialogPane().setContent(passwordField);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                try {
//                    userService.resetPassword(selected.getLogin(), passwordField.getText(), selected.getAdmin());
//                    userService.saveUsers(HelloApplication.DATA_FOLDER);
                    userTable.setItems(FXCollections.observableArrayList(userService.getAll()));
                    showInfo("Пароль изменён.");
                } catch (UserInputException e) {
                    showError(e.getMessage());
                }
            }
        }
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

    @FXML
    private void onStatusAdmin() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите пользователя.");
            return;
        }
        try {
            if (selected.getLogin().equals(CurrentUser.getCurrentUser().getLogin())) {
                showError("Нельзя изменить собственный статус администратора.");
                return;
            }
            userService.doAdmin(selected.getLogin());
//            userService.saveUsers(HelloApplication.DATA_FOLDER);
//            userTable.setItems(FXCollections.observableArrayList(userService.getAll()));
            userTable.getItems().setAll(userService.getAll());
            userTable.refresh();
            showInfo("Роль пользователя изменена.");
        } catch (UserInputException e) {
            showError(e.getMessage());
        }
    }
}