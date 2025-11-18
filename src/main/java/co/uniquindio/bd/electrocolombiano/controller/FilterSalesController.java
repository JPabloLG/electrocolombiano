package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class FilterSalesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label txt_cantidadVentas;

    @FXML
    private ComboBox<String> combo_Month;

    @FXML
    private ComboBox<String> combo_Year;

    @FXML
    private Label txt_header_currentUser;

    private final ElectronicStore store = ElectronicStore.getSingleton();

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("createSale", "ELECTROCOLOMBIANO -Crear Venta-");
    }

    @FXML
    void filter_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        assert combo_Month != null : "fx:id=\"combo_Month\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert combo_Year != null : "fx:id=\"combo_Year\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'filterSales.fxml'.";
        combo_Month.getItems().setAll("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
        combo_Year.getItems().setAll("2025","2024","2023","2022","2021","2020");
    }
}
