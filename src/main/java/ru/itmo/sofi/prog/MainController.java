package ru.itmo.sofi.prog;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick(){
        welcomeText.setText("JavaFX работает");
    }
}
