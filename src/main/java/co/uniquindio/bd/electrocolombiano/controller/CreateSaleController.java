package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.*;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;
import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.services.ProductService;
import co.uniquindio.bd.electrocolombiano.services.SaleService;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CreateSaleController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox box_cantidadCuota;

    @FXML
    private VBox box_cuotaInicial;

    @FXML
    private CheckBox checkCredit;

    @FXML
    private ComboBox<Integer> combo_cuotas;

    @FXML
    private TextField txt_cedulaClient;

    @FXML
    private Label txt_clienteEncontrado;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private TextField txt_idProduct;

    private final ElectronicStore store = ElectronicStore.getSingleton();
    private final SaleService saleService;
    private final SystemUserService systemUserService;
    private final ProductService productService;

    public CreateSaleController(){
        this.saleService = new SaleService(new SaleDAOImpl(JDBC.getConnection()), new UserDAOImpl(JDBC.getConnection()));
        this.systemUserService = new SystemUserService(new UserDAOImpl(JDBC.getConnection()));
        this.productService = new ProductService(new ProductDAOImpl(JDBC.getConnection()), new ProductCategoryDAOImpl(JDBC.getConnection()));
    }

    @FXML
    void addProduct_btn(ActionEvent event) {
       /* String idProduct = txt_idProduct.getText();
        String numberProductsText = txt_numberProducts.getText();
        ProductDTO product = new ProductDTO(idProduct,
        */
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
        try {
            String cedula = txt_cedulaClient.getText();
            UserDTO user = systemUserService.getUserDAO().findByCedula(cedula);
            txt_clienteEncontrado.setText("Cliente encontrado: " + user.getFullName());
        }
        catch (Exception e) {
            txt_clienteEncontrado.setText("Cliente no encontrado" );
        }
    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        assert checkCredit != null : "fx:id=\"checkCredit\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_cedulaClient != null : "fx:id=\"txt_cedulaClient\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_clienteEncontrado != null : "fx:id=\"txt_clienteEncontrado\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'createSale.fxml'.";
        assert txt_idProduct != null : "fx:id=\"txt_idProduct\" was not injected: check your FXML file 'createSale.fxml'.";

        // Inicializar ComboBox con cuotas
        combo_cuotas.getItems().addAll(12, 18, 24);

        // Ocultar Y deshabilitar los VBox inicialmente
        box_cantidadCuota.setVisible(false);
        box_cantidadCuota.setDisable(true);
        box_cuotaInicial.setVisible(false);
        box_cuotaInicial.setDisable(true);

        // Agregar listener al CheckBox para mostrar/ocultar los VBox
        checkCredit.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Mostrar/ocultar Y habilitar/deshabilitar los VBox
            box_cantidadCuota.setVisible(newValue);
            box_cantidadCuota.setDisable(!newValue);
            box_cuotaInicial.setVisible(newValue);
            box_cuotaInicial.setDisable(!newValue);

            // Deshabilitar/habilitar el ComboBox directamente
            combo_cuotas.setDisable(!newValue);

            if (!newValue) {
                // Limpiar la selección del ComboBox cuando se desactiva el crédito
                combo_cuotas.getSelectionModel().clearSelection();
            }
        });
    }
}