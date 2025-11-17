package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SplittableRandom;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.ProductCategoryDAO;
import co.uniquindio.bd.electrocolombiano.dao.ProductCategoryDAOImpl;
import co.uniquindio.bd.electrocolombiano.dao.ProductDAOImpl;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;
import co.uniquindio.bd.electrocolombiano.services.ProductService;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
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
    private ComboBox<String> combo_category;

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
    
    private final ProductService productService;

    public CreateProductController() {
        this.productService = new ProductService(new ProductDAOImpl(JDBC.getConnection()), new ProductCategoryDAOImpl(JDBC.getConnection()));
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void calculatePrice_btn(ActionEvent event) {
        String categoryStr = combo_category.getValue();
        ProductCategoryDTO productCategoryDTO = productService.getCategory(categoryStr);
        BigDecimal iva = productCategoryDTO.getIva();
        BigDecimal margin = productCategoryDTO.getProfitMargin();
        txt_iva.setText(String.valueOf(iva));
        txt_profitMargin.setText(String.valueOf(margin));
    }

    @FXML
    void saveProduct_btn(ActionEvent event) {
        String stock_text = txt_stock.getText();
        int stock =  Integer.parseInt(stock_text);
        String unitPrice_text = txt_unitPrice.getText();
        BigDecimal unitPrice = new BigDecimal(unitPrice_text);
        String purchaseValue_text = txt_purchaseValue.getText();
        BigDecimal purchaseValue = new BigDecimal(purchaseValue_text);
        String categoryStr = combo_category.getValue();
        ProductCategoryDTO productCategoryDTO = productService.getCategory(categoryStr);

        ProductDTO producto = new ProductDTO("", unitPrice, purchaseValue, stock, productCategoryDTO);

        productService.createProduct(producto);
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
        combo_category.getItems().addAll("Audio", "Video", "Tecnologia", "Cocina");
    }

}
