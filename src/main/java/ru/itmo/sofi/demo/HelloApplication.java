package ru.itmo.sofi.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.Parent;
import ru.itmo.sofi.login.CurrentUser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class HelloApplication extends Application {
    public static final Path DATA_FOLDER = Path.of("storage");
    @Override
    public void start(Stage stage) throws IOException {
//        String ownerUsername = askUsername();
//        if (ownerUsername == null) {
//            Platform.exit();
//            return;
//        }
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
//        Parent root = fxmlLoader.load();
//        HelloController controller = fxmlLoader.getController();
//        controller.setOwnerUsername(ownerUsername);
//        Scene scene = new Scene(root, 1025, 400);
//        stage.setTitle("Основное окно");
//        stage.setScene(scene);
//        stage.show();
        FXMLLoader loginLoader = new FXMLLoader(
                HelloApplication.class.getResource("login.fxml")
        );
        Parent loginRoot = loginLoader.load();
        Stage loginStage = new Stage();
        loginStage.setTitle("Авторизация");
        loginStage.setScene(new Scene(loginRoot));
        loginStage.showAndWait();
        if (!CurrentUser.isLoggedIn()) {
            Platform.exit();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("main.fxml")
        );
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        if (controller.hasCriticalLoadError()) {
            Platform.exit();
            return;
        }
        Scene scene = new Scene(root, 1025, 400);
        stage.setTitle("Основное окно");
        stage.setScene(scene);
        stage.show();
    }

    private String askUsername() {
        TextInputDialog dialog = new TextInputDialog("SYSTEM");
        dialog.setTitle("Авторизация");
        dialog.setHeaderText("Добро пожаловать в запись");
        dialog.setContentText("Введите имя пользователя:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String username = result.get().trim();
            if (username.isEmpty()) {
                return "SYSTEM";
            }
            return username;
        }
        return null;
    }

//    private String askUsername() {
//        TextInputDialog dialog = new TextInputDialog("SYSTEM");
//        dialog.setTitle("Авторизация");
//        dialog.setHeaderText("Добро пожаловать в запись");
//        dialog.setContentText("Введите имя пользователя:");
//        Optional<String> result = dialog.showAndWait();
//        if (result.isPresent() && !result.get().trim().isEmpty()) {
//            return result.get().trim();
//        }
//        return "SYSTEM";
//    }

}