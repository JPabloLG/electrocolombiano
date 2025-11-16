package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateSaleController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox checkCredit;

    @FXML
    private TextField txt_cedulaClient;

    @FXML
    private Label txt_clienteEncontrado;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private TextField txt_idProduct;

    @FXML
    private TextField txt_numberCuotes;

    @FXML
    private TextField txt_numberProducts;

    @FXML
    void addProduct_btn(ActionEvent event) {

    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void finishSale_btn(ActionEvent event) throws IOException {
        App.setRoot("finishSale", "ELECTROCOLOMBIANO -Resumen de Venta-");
    }

    @FXML
    void searchClient_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert checkCredit != null : "fx:id=\"checkCredit\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_cedulaClient != null : "fx:id=\"txt_cedulaClient\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_clienteEncontrado != null : "fx:id=\"txt_clienteEncontrado\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_idProduct != null : "fx:id=\"txt_idProduct\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_numberCuotes != null : "fx:id=\"txt_numberCuotes\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_numberProducts != null : "fx:id=\"txt_numberProducts\" was not injected: check your FXML file 'createSale.fxml'.";
    }
}
