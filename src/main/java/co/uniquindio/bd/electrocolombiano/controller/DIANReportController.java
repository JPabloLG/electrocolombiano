package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class DIANReportController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private DatePicker lastDate;

    @FXML
    private DatePicker startDate;

    @FXML
    private Label txt_header_currentUser;

    private final ElectronicStore store = ElectronicStore.getSingleton();


    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void generatePDF_btn(ActionEvent event) {

    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        assert lastDate != null : "fx:id=\"lastDate\" was not injected: check your FXML file 'DIANreport.fxml'.";
        assert startDate != null : "fx:id=\"startDate\" was not injected: check your FXML file 'DIANreport.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'DIANreport.fxml'.";

    }

}
