package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void saveCustomer_btn(ActionEvent event) {

    }

    @FXML
    void selectPhoto(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert img_cliente != null : "fx:id=\"img_cliente\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_fullName != null : "fx:id=\"txt_fullName\" was not injected: check your FXML file 'createCustomer.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'createCustomer.fxml'.";

    }

}
