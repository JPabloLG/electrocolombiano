package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.LoginDTO;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;



public class LoginController {

    private final SystemUserService systemUserService;

    public LoginController() {
        this.systemUserService = new SystemUserService(new UserDAOImpl(JDBC.getConnection()));
    }



    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label forgot_password;

    @FXML
    private Button login_btn;

    @FXML
    private PasswordField txt_password;

    @FXML
    private TextField txt_username;

    @FXML
    void register_btn(MouseEvent event) throws IOException {
        App.setRoot("register", "ELECTROCOLOMBIANO -Registro-");
    }

    @FXML
    void login_btn(ActionEvent event) throws Exception {
        LoginDTO loginDTO = new LoginDTO(txt_username.getText(), txt_password.getText());
        systemUserService.login(loginDTO);
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void initialize() {

    }

}
