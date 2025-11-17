package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.RegisterDTO;
import co.uniquindio.bd.electrocolombiano.model.Rol;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private final SystemUserService systemUserService;

    public RegisterController() {
        this.systemUserService = new SystemUserService(new UserDAOImpl(JDBC.getConnection()));
    }

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
        try {
            String username = txt_username.getText();
            String password = txt_password.getText();
            String cedula = txt_cedula.getText();
            String fullname = txt_fullName.getText();
            String rolSeleccionado = combo_Role.getValue();

            if (username.isEmpty() || password.isEmpty() || cedula.isEmpty() ||
                    fullname.isEmpty() || rolSeleccionado == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
                return;
            }

            // Crear objeto Rol (el id se asignará automáticamente en la base de datos)
            Rol rol = Rol.builder()
                    .roleName(rolSeleccionado)
                    .build();

            RegisterDTO registerDTO = new RegisterDTO(username, password, cedula, rol, fullname);

            systemUserService.register(registerDTO);

            mostrarAlerta("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);

            App.setRoot("login", "ELECTROCOLOMBIANO -Inicio de Sesión-");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar usuario: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        // Inicializar ComboBox con roles
        combo_Role.getItems().addAll("Administrador", "Vendedor");

        // Validar que todos los componentes FXML se hayan inyectado correctamente
        assert combo_Role != null : "fx:id=\"combo_Role\" was not injected: check your FXML file 'register.fxml'.";
        assert register_btn != null : "fx:id=\"register_btn\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_fullName != null : "fx:id=\"txt_fullName\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_password != null : "fx:id=\"txt_password\" was not injected: check your FXML file 'register.fxml'.";
        assert txt_username != null : "fx:id=\"txt_username\" was not injected: check your FXML file 'register.fxml'.";
    }

    /**
     * Método para mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}