package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.CustomerDTO;
import co.uniquindio.bd.electrocolombiano.dto.LoginDTO;
import co.uniquindio.bd.electrocolombiano.dto.RegisterDTO;
import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.model.Rol;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import co.uniquindio.bd.electrocolombiano.util.ShowAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class CreateCustomerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView img_cliente;

    @FXML
    private TextField txt_cedula;

    @FXML
    private TextField txt_fullName;

    @FXML
    private Label txt_header_currentUser;

    private final SystemUserService systemUserService;
    private final ElectronicStore store = ElectronicStore.getSingleton();
    public final ShowAlert showAlert = new ShowAlert();

    public CreateCustomerController(){
        this.systemUserService = new SystemUserService(new UserDAOImpl(JDBC.getConnection()));
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void saveCustomer_btn(ActionEvent event) throws Exception {

        try {
            Rol rol = new Rol("Cliente", 3);
            String cedula = txt_cedula.getText();
            String fullName = txt_fullName.getText();
            String userName = ("DEFAULT " + fullName);
            String password = ("DEFAULT "+cedula);
            RegisterDTO customerDTO = new RegisterDTO(userName, password, cedula , rol, fullName);
            systemUserService.register(customerDTO);

            System.out.println("Cliente creado: " + userName);
            showAlert.mostrarAlerta("Éxito", "Cliente creado correctamente", Alert.AlertType.INFORMATION);
            App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");

        } catch (Exception e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
            showAlert.mostrarAlerta("Error", "Error al crear el cliente", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void selectPhoto(ActionEvent event) {
        showAlert.mostrarAlerta("Error", "Esta funcionalidad está en desarrollo", Alert.AlertType.ERROR);
    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        assert img_cliente != null : "fx:id=\"img_cliente\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_fullName != null : "fx:id=\"txt_fullName\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'createCustomer.fxml'.";

    }

}
