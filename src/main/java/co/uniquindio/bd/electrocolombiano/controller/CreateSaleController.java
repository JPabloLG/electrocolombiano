package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.dao.*;
import co.uniquindio.bd.electrocolombiano.dto.*;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.model.Product;
import co.uniquindio.bd.electrocolombiano.model.Sale;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
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
            mostrarAlerta("Error", "Por favor ingrese un nombre de producto");
            return;
        }

        // Buscar el producto por nombre
        ProductDTO product = productService.findByName(nameProduct);

        if (product != null) {
            // Verificar stock
            if (product.getStock() <= 0) {
                txt_productFind.setText("❌ Producto sin stock disponible");
                txt_productFind.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            // Buscar si el producto ya está en la lista
            ProductDTO productoExistente = buscarProductoEnLista(product.getId());

            if (productoExistente != null) {
                // Ya existe: incrementar cantidad
                int nuevaCantidad = productoExistente.getQuantity() + 1;

                // Verificar que no exceda el stock
                if (nuevaCantidad > product.getStock()) {
                    mostrarAlerta("Error", "Stock insuficiente. Disponible: " + product.getStock());
                    return;
                }

                productoExistente.setQuantity(nuevaCantidad);

                txt_productFind.setText("✓ " + product.getName() + " (Cantidad: " + nuevaCantidad + ")");

            } else {
                // No existe: agregar nuevo con cantidad = 1
                ProductDTO productoParaVenta = product.toBuilder()
                        .quantity(1)
                        .build();

                productosSeleccionados.add(productoParaVenta);

                txt_productFind.setText("✓ " + product.getName() + " (Cantidad: 1)");
            }

            // Recalcular totales
            subtotal = calcularSubtotal();
            total = calcularTotalConIVA();

            txt_productFind.setStyle("-fx-text-fill: #27ae60;");
            txt_nameProduct.clear();

            mostrarAlerta("Éxito", "Producto agregado: " + product.getName() +
                    "\nCantidad: " + (productoExistente != null ? productoExistente.getQuantity() : 1) +
                    "\nSubtotal: $" + String.format("%,.2f", subtotal) +
                    "\nTotal con IVA: $" + String.format("%,.2f", total));

        } else {
            txt_productFind.setText("❌ Producto no encontrado");
            txt_productFind.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private ProductDTO buscarProductoEnLista(String productId) {
        for (ProductDTO producto : productosSeleccionados) {
            if (producto.getId().equals(productId)) {
                return producto;
            }
        }
        return null;
    }

    private BigDecimal calcularSubtotal() {
        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        for (ProductDTO producto : productosSeleccionados) {
            BigDecimal precioProducto = producto.getUnitPrice()
                    .multiply(BigDecimal.valueOf(producto.getQuantity()));
            subtotalCalculado = subtotalCalculado.add(precioProducto);
        }

        return subtotalCalculado;
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void filterSale_btn(ActionEvent event) throws IOException {
        App.setRoot("filterSales", "ELECTROCOLOMBIANO -Filtra tus ventas-");
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
            System.out.println(checkCredit.isSelected());

            try {
                // Determinar si es crédito
                boolean esCredito = checkCredit.isSelected();

                UserDTO empleado = new UserDTO(
                        store.getCurrentUser().getCedula(),
                        store.getCurrentUser().getFullName(),
                        store.getCurrentUser().getUserName(),
                        store.getCurrentUser().getPassword(),
                        store.getCurrentUser().getRole()
                );
                System.out.println(empleado);
                UserDTO cliente = systemUserService.getUser(txt_cedulaClient.getText());

                BigDecimal cuotaInicial = BigDecimal.ZERO;
                Integer numeroCuotas = null;

                // Validar campos de crédito si es necesario
                if (esCredito) {
                    if (combo_cuotas.getValue() == null || txt_inicialCuota.getText().trim().isEmpty()) {
                        mostrarAlerta("Error", "Para venta a crédito debe seleccionar cuotas e ingresar cuota inicial");
                        return;
                    }

                    try {
                        cuotaInicial = new BigDecimal(txt_inicialCuota.getText().trim());
                        numeroCuotas = combo_cuotas.getValue();

                        // Validar que la cuota inicial sea al menos el 30%
                        BigDecimal minimoInicial = total.multiply(new BigDecimal("0.30"));
                        if (cuotaInicial.compareTo(minimoInicial) < 0) {
                            mostrarAlerta("Error",
                                    "La cuota inicial debe ser al menos el 30% del total\n" +
                                            "Mínimo requerido: $" + String.format("%,.2f", minimoInicial));
                            return;
                        }

                        // Validar que la cuota inicial no sea mayor al total
                        if (cuotaInicial.compareTo(total) >= 0) {
                            mostrarAlerta("Error", "La cuota inicial no puede ser mayor o igual al total");
                            return;
                        }

                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error", "La cuota inicial debe ser un valor numérico válido");
                        return;
                    }
                }
                System.out.println("Numero de cuotas: "+ numeroCuotas);
                // ========== CREAR LA VENTA ==========
                SaleDTO sale = SaleDTO.builder()
                        .employee(empleado)
                        .customerId(cliente.getCedula())
                        .subtotal(subtotal)
                        .totalPrice(total)
                        .isCredit(esCredito)
                        .products(new ArrayList<>(productosSeleccionados))
                        .payments(new ArrayList<>())
                        .build();

                SaleDTO ventaCreada = saleService.createSale(sale);

                System.out.println("✓ Venta creada con ID: " + ventaCreada.getId());

                List<PaymentDTO> payments = new ArrayList<>();

                if (esCredito) {
                    // VENTA A CRÉDITO: Crear cuota inicial + cuotas mensuales
                    System.out.println("=== CREANDO PAGOS A CRÉDITO ===");
                    System.out.println("Cuota inicial: $" + cuotaInicial);
                    System.out.println("Número de cuotas: " + numeroCuotas);
                    String idPagoInicial = UUID.randomUUID().toString();
                    // 1. Crear pago de cuota inicial
                    PaymentDTO pagoInicial = PaymentDTO.builder()
                            .id(idPagoInicial)
                            .saleId(ventaCreada.getId())
                            .totalPrice(cuotaInicial)
                            .isCredit(false)
                            .installmentCount(0)
                            .build();

                    System.out.println("Guardando pago inicial: " + pagoInicial.getId());
                    paymentService.createPayment(pagoInicial);
                    payments.add(pagoInicial);
                    System.out.println("✓ Pago inicial guardado");

                    // 2. Crear pago a crédito con cuotas
                    BigDecimal saldoFinanciar = total.subtract(cuotaInicial);
                    BigDecimal interes = saldoFinanciar.multiply(new BigDecimal("0.05")); // 5% de interés
                    BigDecimal totalFinanciado = saldoFinanciar.add(interes);
                    BigDecimal valorCuota = totalFinanciado.divide(
                            BigDecimal.valueOf(numeroCuotas),
                            2,
                            RoundingMode.HALF_UP
                    );

                    PaymentDTO pagoCredito = PaymentDTO.builder()
                            .id(UUID.randomUUID().toString())
                            .saleId(ventaCreada.getId())
                            .totalPrice(totalFinanciado)
                            .isCredit(true)
                            .installments(new ArrayList<>())
                            .installmentCount(numeroCuotas)
                            .build();

                    System.out.println("=== PAGO A CRÉDITO ===");
                    System.out.println("ID: " + pagoCredito.getId());
                    System.out.println("Total financiado: $" + totalFinanciado);
                    System.out.println("Número de cuotas: " + numeroCuotas);

                    // 3. Generar cuotas mensuales
                    List<InstallmentDTO> installments = new ArrayList<>();
                    for (int i = 1; i <= numeroCuotas; i++) {
                        InstallmentDTO installment = InstallmentDTO.builder()
                                .id(UUID.randomUUID().toString())
                                .paymentId(pagoCredito.getId())
                                .installmentCount(i)
                                .installmentValue(valorCuota)
                                .installmentDate(LocalDate.now().plusMonths(i))
                                .isPaid(false)
                                .build();

                        installments.add(installment);
                        System.out.println("  Cuota #" + i + ": $" + valorCuota + " | Vence: " + installment.getInstallmentDate());
                    }
                    System.out.println("Cantidad de cuotas: "+ numeroCuotas);
                    pagoCredito.setInstallments(installments);
                    paymentService.createPayment(pagoCredito);
                    payments.add(pagoCredito);

                    // Mostrar resumen de crédito
                    mostrarAlerta("Venta a Crédito Creada",
                            "ID: " + ventaCreada.getId() + "\n" +
                                    "Total: $" + String.format("%,.2f", total) + "\n\n" +
                                    "Cuota Inicial: $" + String.format("%,.2f", cuotaInicial) + "\n" +
                                    "Saldo a Financiar: $" + String.format("%,.2f", saldoFinanciar) + "\n" +
                                    "Interés (5%): $" + String.format("%,.2f", interes) + "\n" +
                                    "Total Financiado: $" + String.format("%,.2f", totalFinanciado) + "\n\n" +
                                    "Número de Cuotas: " + numeroCuotas + "\n" +
                                    "Valor por Cuota: $" + String.format("%,.2f", valorCuota) + "\n" +
                                    "Primera Cuota: " + LocalDate.now().plusMonths(1)
                    );

                } else {
                    // VENTA DE CONTADO: Un solo pago por el total
                    String idPagoContado = UUID.randomUUID().toString();
                    PaymentDTO pagoContado = PaymentDTO.builder()
                            .id(idPagoContado)
                            .saleId(ventaCreada.getId())
                            .totalPrice(total)
                            .isCredit(false)
                            .build();

                    paymentService.createPayment(pagoContado);
                    payments.add(pagoContado);

                    // Mostrar resumen de contado
                    mostrarAlerta("Venta de Contado Creada",
                            "ID: " + ventaCreada.getId() + "\n" +
                                    "Total Pagado: $" + String.format("%,.2f", total) + "\n" +
                                    "Fecha: " + LocalDate.now()
                    );
                }

                // ========== ACTUALIZAR MODELO LOCAL ==========
                ventaCreada.setPayments(payments);

                // Crear entidad SystemUser para el cliente
                SystemUser systemUserCliente = SystemUser.builder()
                        .userName(cliente.getUserName())
                        .cedula(cliente.getCedula())
                        .role(cliente.getRole())
                        .fullName(cliente.getFullName())
                        .password(cliente.getPassword())
                        .build();

                // Crear entidad Sale para el store
                Sale saleEntity = Sale.builder()
                        .id(ventaCreada.getId())
                        .saleDate(ventaCreada.getDateSale())
                        .customer(systemUserCliente)
                        .employee(store.getCurrentUser())
                        .payments(new ArrayList<>()) // Se cargan después si es necesario
                        .products(new ArrayList<>()) // Se cargan después si es necesario
                        .subtotal(ventaCreada.getSubtotal())
                        .totalPrice(ventaCreada.getTotalPrice())
                        .isCredit(ventaCreada.isCredit())
                        .build();

                store.setSale(saleEntity);

                // ========== LIMPIAR Y NAVEGAR ==========
                limpiarFormulario();
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
                // VERIFICAR QUE EL ROL SEA CLIENTE (usando String)
                if ("CLIENTE".equals(user.getRole().getRoleName())) {
                    clienteEncontrado = user;
                    txt_clienteEncontrado.setText("✓ Cliente: " + user.getUserName());
                    txt_clienteEncontrado.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    clienteEncontrado = null;
                    txt_clienteEncontrado.setText("❌ La cédula no pertenece a un cliente");
                    txt_clienteEncontrado.setStyle("-fx-text-fill: #e74c3c;");
                    mostrarAlerta("Error", "La cédula pertenece a un " + user.getRole().getRoleName() + ", no a un cliente");
                }
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