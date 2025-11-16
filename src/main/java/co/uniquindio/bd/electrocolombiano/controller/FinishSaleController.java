package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FinishSaleController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label txt_cedula;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private Label txt_name;

    @FXML
    private Label txt_paymentType;

    @FXML
    private Label txt_total;


    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("createSale", "ELECTROCOLOMBIANO -Crear Venta-");
    }

    @FXML
    void finishProcess_btn(ActionEvent event) {

    }

    @FXML
    void generate_pdf_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_name != null : "fx:id=\"txt_name\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_paymentType != null : "fx:id=\"txt_paymentType\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_total != null : "fx:id=\"txt_total\" was not injected: check your FXML file 'finishSale.fxml'.";
    }
}
