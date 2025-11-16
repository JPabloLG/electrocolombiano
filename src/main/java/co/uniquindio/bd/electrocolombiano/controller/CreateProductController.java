package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateProductController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<?> combo_category;

    @FXML
    private Label txt_clienteEncontrado;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private TextField txt_iva;

    @FXML
    private TextField txt_nameProduct;

    @FXML
    private TextField txt_profitMargin;

    @FXML
    private TextField txt_purchaseValue;

    @FXML
    private TextField txt_stock;

    @FXML
    private TextField txt_unitPrice;

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void calculatePrice_btn(ActionEvent event) {

    }

    @FXML
    void saveProduct_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert combo_category != null : "fx:id=\"combo_category\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_clienteEncontrado != null : "fx:id=\"txt_clienteEncontrado\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_iva != null : "fx:id=\"txt_iva\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_nameProduct != null : "fx:id=\"txt_nameProduct\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_profitMargin != null : "fx:id=\"txt_profitMargin\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_purchaseValue != null : "fx:id=\"txt_purchaseValue\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_stock != null : "fx:id=\"txt_stock\" was not injected: check your FXML file 'createProduct.fxml'.";
        assert txt_unitPrice != null : "fx:id=\"txt_unitPrice\" was not injected: check your FXML file 'createProduct.fxml'.";
    }

}
