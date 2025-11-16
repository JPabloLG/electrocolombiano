package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Builder;


public class RegisterController {



    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> combo_Role;

    @FXML
    private Button register_btn;

    @FXML
    private TextField txt_cedula;

    @FXML
    private TextField txt_fullName;

    @FXML
    private PasswordField txt_password;

    @FXML
    private TextField txt_username;

    @FXML
    void register_btn(ActionEvent event) throws IOException {
        //Toda la logica


        App.setRoot("login", "ELECTROCOLOMBIANO -Inicio de Sesión-");
    }

    @FXML
    void initialize() {
        assert combo_Role != null : "fx:id=\"combo_Role\" was not injected: check your FXML file 'register.fxml'.";
        assert register_btn != null : "fx:id=\"register_btn\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_fullName != null : "fx:id=\"txt_fullName\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_password != null : "fx:id=\"txt_password\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_username != null : "fx:id=\"txt_username\" was not injected: check your FXML file 'register.fxml'.";
        combo_Role.getItems().addAll("Administrador", "Vendedor");
    }

}
