package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.ProductCategoryDAOImpl;
import co.uniquindio.bd.electrocolombiano.dao.ProductDAOImpl;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.services.ProductService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListProductsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<ProductDTO, String> column_PurchaseValue;

    @FXML
    private TableColumn<ProductDTO, String> column_nombre;

    @FXML
    private TableColumn<ProductDTO, String> column_stock;

    @FXML
    private TableColumn<ProductDTO, String> column_unitPrice;

    @FXML
    private ComboBox<String> combo_category;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private TableView<ProductDTO> table_products; // AGREGAR ESTA LÍNEA

    private final ElectronicStore store = ElectronicStore.getSingleton();
    private final ProductService productService;
    private final ObservableList<ProductDTO> productosObservable = FXCollections.observableArrayList();

    public ListProductsController(){
        this.productService = new ProductService(new ProductDAOImpl(JDBC.getConnection()), new ProductCategoryDAOImpl(JDBC.getConnection()));
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("createProduct", "ELECTROCOLOMBIANO -Crear Producto-");
    }

    @FXML
    void listProducts_btn(ActionEvent event) {
        String categoriaSeleccionada = combo_category.getValue();

        if (categoriaSeleccionada == null || categoriaSeleccionada.isEmpty()) {
            mostrarAlerta("Error", "Por favor seleccione una categoría");
            return;
        }

        try {
            List<ProductDTO> productos = productService.findByCategory(categoriaSeleccionada);

            // Limpiar la tabla
            productosObservable.clear();

            if (productos != null && !productos.isEmpty()) {
                // Agregar productos a la lista observable
                productosObservable.addAll(productos);

                // Configurar la tabla con los datos
                table_products.setItems(productosObservable);

                mostrarAlerta("Éxito", "Se encontraron " + productos.size() + " productos en la categoría " + categoriaSeleccionada);
            } else {
                mostrarAlerta("Información", "No se encontraron productos en la categoría " + categoriaSeleccionada);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());

        // Configurar las columnas de la tabla
        configurarColumnas();

        // Cargar categorías en el ComboBox
        combo_category.getItems().addAll("Audio", "Video", "Tecnologia", "Cocina");

        // Verificar inyección de dependencias
        assert column_PurchaseValue != null : "fx:id=\"column_PurchaseValue\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert column_nombre != null : "fx:id=\"column_nombre\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert column_stock != null : "fx:id=\"column_stock\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert column_unitPrice != null : "fx:id=\"column_unitPrice\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert combo_category != null : "fx:id=\"combo_category\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'listProducts.fxml'.";
        assert table_products != null : "fx:id=\"table_products\" was not injected: check your FXML file 'listProducts.fxml'.";
    }

    private void configurarColumnas() {
        // Configurar columna Nombre
        column_nombre.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Configurar columna Precio Unitario
        column_unitPrice.setCellValueFactory(cellData -> {
            ProductDTO producto = cellData.getValue();
            return new SimpleStringProperty("$" + String.format("%,.2f", producto.getUnitPrice()));
        });

        // Configurar columna Valor de Compra
        column_PurchaseValue.setCellValueFactory(cellData -> {
            ProductDTO producto = cellData.getValue();
            return new SimpleStringProperty("$" + String.format("%,.2f", producto.getPurchaseValue()));
        });

        // Configurar columna Stock
        column_stock.setCellValueFactory(cellData -> {
            ProductDTO producto = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(producto.getStock()));
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}