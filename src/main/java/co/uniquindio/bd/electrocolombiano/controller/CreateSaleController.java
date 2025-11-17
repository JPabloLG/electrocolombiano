package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.*;
import co.uniquindio.bd.electrocolombiano.dto.*;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.services.PaymentService;
import co.uniquindio.bd.electrocolombiano.services.ProductService;
import co.uniquindio.bd.electrocolombiano.services.SaleService;
import co.uniquindio.bd.electrocolombiano.services.SystemUserService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private Label txt_productFind;

    @FXML
    private Label txt_header_currentUser;

    @FXML
    private TextField txt_nameProduct;

    @FXML
    private TextField txt_inicialCuota;

    // Variables para almacenar datos temporales
    private List<ProductDTO> productosSeleccionados = new ArrayList<>();
    private UserDTO clienteEncontrado = null;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    private final ElectronicStore store = ElectronicStore.getSingleton();
    private final SaleService saleService;
    private final SystemUserService systemUserService;
    private final ProductService productService;
    private final PaymentService paymentService;

    public CreateSaleController(){
        this.paymentService = new PaymentService(new PaymentDAOImpl(JDBC.getConnection()), new InstallmentDAOImpl(JDBC.getConnection()));
        this.saleService = new SaleService(new SaleDAOImpl(JDBC.getConnection()), new UserDAOImpl(JDBC.getConnection()), paymentService);
        this.systemUserService = new SystemUserService(new UserDAOImpl(JDBC.getConnection()));
        this.productService = new ProductService(new ProductDAOImpl(JDBC.getConnection()), new ProductCategoryDAOImpl(JDBC.getConnection()));
    }

    @FXML
    void addProduct_btn(ActionEvent event) {
        String nameProduct = txt_nameProduct.getText().trim();

        if (nameProduct.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese un ID de producto");
            return;
        }
            // Buscar el producto por ID usando el DAO
            ProductDTO product = productService.findByName(nameProduct);

            if (product != null) {
                // Verificar stock
                if (product.getStock() <= 0) {
                    txt_productFind.setText("❌ Producto sin stock disponible");
                    txt_productFind.setStyle("-fx-text-fill: #e74c3c;");
                    return;
                }

                // Agregar producto a la lista
                productosSeleccionados.add(product);

                // Calcular subtotal y total
                subtotal = subtotal.add(product.getUnitPrice());
                total = calcularTotalConIVA();

                // Actualizar interfaz
                txt_productFind.setText("✓ " + product.getName() + " - $" + product.getUnitPrice());
                txt_productFind.setStyle("-fx-text-fill: #27ae60;");

                // Limpiar campo
                txt_nameProduct.clear();

                mostrarAlerta("Éxito", "Producto agregado: " + product.getName());

            } else {
                txt_productFind.setText("❌ Producto no encontrado");
                txt_productFind.setStyle("-fx-text-fill: #e74c3c;");
            }
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void finishSale_btn(ActionEvent event) throws IOException {
        // Validaciones antes de crear la venta
        if (clienteEncontrado == null) {
            mostrarAlerta("Error", "Debe buscar y encontrar un cliente primero");
            return;
        }

        if (productosSeleccionados.isEmpty()) {
            mostrarAlerta("Error", "Debe agregar al menos un producto");
            return;
        }

        try {
            // Determinar si es crédito
            boolean esCredito = checkCredit.isSelected();

            // Validar campos de crédito si es necesario
            if (esCredito) {
                if (combo_cuotas.getValue() == null || txt_inicialCuota.getText().trim().isEmpty()) {
                    mostrarAlerta("Error", "Para venta a crédito debe seleccionar cuotas e ingresar cuota inicial");
                    return;
                }
            }

            UserDTO user = new UserDTO(store.getCurrentUser().getCedula(), store.getCurrentUser().getFullName(), store.getCurrentUser().getUserName(), store.getCurrentUser().getPassword(), store.getCurrentUser().getRole());
            System.out.println(user);
            UserDTO cliente = systemUserService.getUser(txt_cedulaClient.getText());
            // Crear el DTO de la venta usando el builder
            SaleDTO sale = SaleDTO.builder()
                    .employee(user) // Empleado actual
                    .customerId(cliente.getCedula())
                    .subtotal(subtotal)
                    .totalPrice(total)
                    .isCredit(esCredito)
                    .products(new ArrayList<>(productosSeleccionados))
                    .payments(new ArrayList<>())
                    .build();


            SaleDTO ventaCreada = saleService.createSale(sale);

            // Mostrar mensaje de éxito
            mostrarAlerta("Éxito", "Venta creada exitosamente\nID: " + ventaCreada.getId() +
                    "\nTotal: $" + ventaCreada.getTotalPrice() +
                    "\nTipo: " + (esCredito ? "Crédito" : "Contado"));

            // Limpiar formulario
            limpiarFormulario();

            // Ir a la pantalla de resumen
            App.setRoot("finishSale", "ELECTROCOLOMBIANO -Resumen de Venta-");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void searchClient_btn(ActionEvent event) {
        String cedula = txt_cedulaClient.getText().trim();

        if (cedula.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese una cédula");
            return;
        }

        try {
            UserDTO user = systemUserService.getUserDAO().findByCedula(cedula);
            if (user != null) {
                clienteEncontrado = user;
                txt_clienteEncontrado.setText("✓ Cliente: " + user.getFullName());
                txt_clienteEncontrado.setStyle("-fx-text-fill: #27ae60;");
            } else {
                clienteEncontrado = null;
                txt_clienteEncontrado.setText("❌ Cliente no encontrado");
                txt_clienteEncontrado.setStyle("-fx-text-fill: #e74c3c;");
            }
        } catch (Exception e) {
            clienteEncontrado = null;
            txt_clienteEncontrado.setText("Error al buscar cliente");
            txt_clienteEncontrado.setStyle("-fx-text-fill: #e74c3c;");
            mostrarAlerta("Error", "Error al buscar cliente: " + e.getMessage());
        }
    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        txt_productFind.setText("");

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
                txt_inicialCuota.clear();
            }
        });
    }

    // Métodos auxiliares
    private BigDecimal calcularTotalConIVA() {
        BigDecimal totalConIVA = BigDecimal.ZERO;

        for (ProductDTO producto : productosSeleccionados) {
            BigDecimal precio = producto.getUnitPrice();
            BigDecimal iva = producto.getCategory().getIva();
            BigDecimal precioConIVA = precio.multiply(BigDecimal.ONE.add(iva));
            totalConIVA = totalConIVA.add(precioConIVA);
        }

        return totalConIVA;
    }

    private void limpiarFormulario() {
        txt_cedulaClient.clear();
        txt_nameProduct.clear();
        txt_inicialCuota.clear();
        txt_clienteEncontrado.setText("Cliente no encontrado");
        txt_clienteEncontrado.setStyle("-fx-text-fill: #e74c3c;");
        txt_productFind.setText("Producto no encontrado");
        txt_productFind.setStyle("-fx-text-fill: #e74c3c;");
        checkCredit.setSelected(false);
        combo_cuotas.getSelectionModel().clearSelection();

        productosSeleccionados.clear();
        clienteEncontrado = null;
        subtotal = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}