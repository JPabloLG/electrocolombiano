package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.util.ShowAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class PrincipalController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private Label txt_principal_currentUser;

    private final ElectronicStore store = ElectronicStore.getSingleton();
    public final ShowAlert showAlert = new ShowAlert();

    @FXML
    void logout_btn(ActionEvent event) throws IOException {
        store.setCurrentUser(null);
        showAlert.mostrarAlerta("Éxito", "Se cerró sesión correctamente", Alert.AlertType.INFORMATION);
        App.setRoot("login", "ELECTROCOLOMBIANO -Inicio de Sesión-");
    }

    @FXML
    void createCustomer_btn(ActionEvent event) throws IOException {
        App.setRoot("createCustomer", "ELECTROCOLOMBIANO -Crear Cliente-");
    }

    @FXML
    void createProduct_btn(ActionEvent event) throws IOException {
        App.setRoot("createProduct", "ELECTROCOLOMBIANO -Crear Producto-");
    }

    @FXML
    void createSale_btn(ActionEvent event) throws IOException {
        App.setRoot("createSale", "ELECTROCOLOMBIANO -Crear Venta-");
    }

    @FXML
    void reportDIAN_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        txt_principal_currentUser.setText("PÁGINA PRINCIPAL DE " + store.getCurrentUser().getFullName());
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'principal.fxml'.";
        assert txt_principal_currentUser != null : "fx:id=\"txt_principal_currentUser\" was not injected: check your FXML file 'principal.fxml'.";
    }

}
