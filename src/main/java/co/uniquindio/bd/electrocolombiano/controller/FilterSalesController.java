package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.math.BigDecimal;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import co.uniquindio.bd.electrocolombiano.services.SaleService;
import co.uniquindio.bd.electrocolombiano.dao.SaleDAOImpl;
import co.uniquindio.bd.electrocolombiano.dao.UserDAOImpl;
import co.uniquindio.bd.electrocolombiano.services.PaymentService;
import co.uniquindio.bd.electrocolombiano.util.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class FilterSalesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label txt_cantidadContado;

    @FXML
    private Label txt_cantidadCredito;

    @FXML
    private Label txt_cantidadVentas;

    @FXML
    private Label txt_totalFacturado;

    @FXML
    private ComboBox<String> combo_Month;

    @FXML
    private ComboBox<String> combo_Year;

    @FXML
    private Label txt_header_currentUser;

    private final ElectronicStore store = ElectronicStore.getSingleton();
    private final SaleService saleService;

    // Mapeo de nombres de meses a números
    private final String[] monthNames = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    public FilterSalesController() {
        this.saleService = new SaleService(
                new SaleDAOImpl(JDBC.getConnection()),
                new UserDAOImpl(JDBC.getConnection()),
                new PaymentService(null, null)
        );
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("createSale", "ELECTROCOLOMBIANO -Crear Venta-");
    }

    @FXML
    void filter_btn(ActionEvent event) {
        try {
            // Validar que se hayan seleccionado mes y año
            if (combo_Month.getValue() == null || combo_Year.getValue() == null) {
                mostrarAlerta("Error", "Por favor seleccione un mes y un año");
                return;
            }

            // Convertir mes de texto a número
            String monthName = combo_Month.getValue();
            int monthNumber = getMonthNumber(monthName);

            // Obtener año
            int year = Integer.parseInt(combo_Year.getValue());

            // Obtener el monto total de ventas
            BigDecimal montoTotalVentas = saleService.countSaleByMonth(monthNumber, year);

            // Obtener la cantidad total de ventas
            int cantidadVentas = saleService.countSaleByMothAndYear(monthNumber, year);

            // Obtener cantidad de ventas a crédito
            int ventasCredito = saleService.countSaleCredit(year, monthNumber);

            // Obtener cantidad de ventas de contado
            int ventasContado = saleService.countSale(year, monthNumber);

            // Formatear el monto como dinero
            String montoFormateado = String.format("$%,.2f", montoTotalVentas);

            // Mostrar resultados en los labels correspondientes
            txt_totalFacturado.setText(montoFormateado);
            txt_cantidadVentas.setText(String.valueOf(cantidadVentas));
            txt_cantidadCredito.setText(String.valueOf(ventasCredito));
            txt_cantidadContado.setText(String.valueOf(ventasContado));

            // Mostrar mensaje informativo con todos los datos
            mostrarAlerta("Filtro aplicado",
                    "Resultados para " + monthName + " de " + year + ":\n\n" +
                            "• Cantidad total de ventas: " + cantidadVentas + "\n" +
                            "• Ventas a crédito: " + ventasCredito + "\n" +
                            "• Ventas de contado: " + ventasContado + "\n" +
                            "• Monto total facturado: " + montoFormateado);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El año seleccionado no es válido");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al filtrar ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convierte el nombre del mes a número (1-12)
     */
    private int getMonthNumber(String monthName) {
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthName)) {
                return i + 1; // Los meses en SQL van de 1 a 12
            }
        }
        throw new IllegalArgumentException("Mes no válido: " + monthName);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        txt_header_currentUser.setText(store.getCurrentUser().getFullName());

        // Verificar assertions
        assert combo_Month != null : "fx:id=\"combo_Month\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert combo_Year != null : "fx:id=\"combo_Year\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_cantidadVentas != null : "fx:id=\"txt_cantidadVentas\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_totalFacturado != null : "fx:id=\"txt_totalFacturado\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_cantidadContado != null : "fx:id=\"txt_cantidadContado\" was not injected: check your FXML file 'filterSales.fxml'.";
        assert txt_cantidadCredito != null : "fx:id=\"txt_cantidadCredito\" was not injected: check your FXML file 'filterSales.fxml'.";

        // Inicializar combos
        combo_Month.getItems().setAll("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
        combo_Year.getItems().setAll("2025","2024","2023","2022","2021","2020");

        // Inicializar los labels
        txt_totalFacturado.setText("$0.00");
        txt_cantidadVentas.setText("0");
        txt_cantidadCredito.setText("0");
        txt_cantidadContado.setText("0");
    }
}