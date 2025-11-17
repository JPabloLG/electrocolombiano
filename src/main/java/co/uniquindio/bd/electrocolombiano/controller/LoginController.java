package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.LoginDTO;
import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import co.uniquindio.bd.electrocolombiano.util.ShowAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;



public class LoginController {

    private final SystemUserService systemUserService;
    private final ElectronicStore store = ElectronicStore.getSingleton();
    public final ShowAlert showAlert = new ShowAlert();

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
        try {
            LoginDTO loginDTO = new LoginDTO(txt_username.getText(), txt_password.getText());
            UserDTO userDTO = systemUserService.login(loginDTO);

            SystemUser systemUser = SystemUser.builder()
                    .userName(userDTO.getUserName())
                    .password(userDTO.getPassword())
                    .fullName(userDTO.getFullName())
                    .role(userDTO.getRole())
                    .build();

            store.setCurrentUser(systemUser);
            showAlert.mostrarAlerta("Éxito", "Se inició sesión correctamente", Alert.AlertType.INFORMATION);
            App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");

        } catch (Exception e) {

            System.out.println("Error al iniciar sesión: " + e.getMessage());
            showAlert.mostrarAlerta("Error", "Error al iniciar sesión", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void initialize() {

    }

}
