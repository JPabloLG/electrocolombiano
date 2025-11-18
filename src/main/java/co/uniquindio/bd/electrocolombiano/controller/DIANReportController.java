package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

public class DIANReportController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Integer> combo_trimestre;

    @FXML
    private ComboBox<Integer> combo_year;

    @FXML
    private Label txt_header_currentUser;

    private final ElectronicStore store = ElectronicStore.getSingleton();
    private final SaleService saleService;

    public DIANReportController() {
        this.saleService = new SaleService(
                new SaleDAOImpl(JDBC.getConnection()),
                new UserDAOImpl(JDBC.getConnection()),
                new PaymentService(null, null)
        );
    }

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void generatePDF_btn(ActionEvent event) {
        try {
            // Validar que se hayan seleccionado año y trimestre
            if (combo_year.getValue() == null || combo_trimestre.getValue() == null) {
                mostrarAlerta("Error", "Por favor seleccione un año y un trimestre");
                return;
            }

            int year = combo_year.getValue();
            int quarter = combo_trimestre.getValue();

            // Obtener el total de IVA para el trimestre seleccionado
            BigDecimal totalIVA = saleService.fingIVAPay(year, quarter);

            // Generar el PDF del reporte DIAN
            generarReporteDIAN(year, quarter, totalIVA);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarReporteDIAN(int year, int quarter, BigDecimal totalIVA) {
        try {
            // Crear contenido HTML del reporte DIAN
            String htmlContent = crearContenidoReporteDIAN(year, quarter, totalIVA);

            // Crear un archivo temporal
            java.io.File tempFile = java.io.File.createTempFile("reporte_dian_" + year + "_Q" + quarter + "_", ".html");

            // Escribir el contenido HTML
            java.io.FileWriter writer = new java.io.FileWriter(tempFile);
            writer.write(htmlContent);
            writer.close();

            // Abrir en el navegador
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(tempFile.toURI());
                    mostrarAlerta("Éxito", "Reporte DIAN generado y abierto en el navegador");

                    // Programar eliminación del archivo temporal después de 10 segundos
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    if (tempFile.exists()) {
                                        tempFile.delete();
                                    }
                                }
                            },
                            10000
                    );
                    return;
                }
            }

            // Si Desktop no funciona, intentar con el método antiguo
            abrirConRuntime(tempFile);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private void abrirConRuntime(java.io.File tempFile) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("win")) {
                rt.exec(new String[]{"cmd", "/c", "start", "\"\"", tempFile.getAbsolutePath()});
            } else if (os.contains("mac")) {
                rt.exec(new String[]{"open", tempFile.getAbsolutePath()});
            } else if (os.contains("nix") || os.contains("nux")) {
                rt.exec(new String[]{"xdg-open", tempFile.getAbsolutePath()});
            } else {
                rt.exec(new String[]{"open", tempFile.getAbsolutePath()});
            }

            mostrarAlerta("Éxito", "Reporte DIAN generado y abierto en el navegador");

            // Programar eliminación del archivo temporal
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    },
                    10000
            );

        } catch (Exception e) {
            // Si todo falla, mostrar el contenido en un diálogo
            mostrarContenidoEnDialogo();
        }
    }

    private void mostrarContenidoEnDialogo() {
        try {
            int year = combo_year.getValue();
            int quarter = combo_trimestre.getValue();
            BigDecimal totalIVA = saleService.fingIVAPay(year, quarter);

            String htmlContent = crearContenidoReporteDIAN(year, quarter, totalIVA);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte DIAN - ELECTROCOLOMBIANO");
            alert.setHeaderText("Reporte de IVA Generado");
            alert.setContentText("Reporte DIAN para " + year + " - Trimestre " + quarter + "\n\n" +
                    "Total IVA Recaudado: $" + String.format("%,.2f", totalIVA) + "\n\n" +
                    "Puede imprimir esta información o tomar captura de pantalla.");
            alert.getDialogPane().setPrefSize(400, 300);
            alert.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private String crearContenidoReporteDIAN(int year, int quarter, BigDecimal totalIVA) {
        String fechaGeneracion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String periodo = obtenerPeriodoTrimestre(quarter);
        String rangoFechas = obtenerRangoFechas(year, quarter);

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reporte DIAN - ELECTROCOLOMBIANO</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 20px;\n" +
                "            background-color: #f5f5f5;\n" +
                "        }\n" +
                "        .reporte-container {\n" +
                "            max-width: 800px;\n" +
                "            margin: 0 auto;\n" +
                "            background: white;\n" +
                "            padding: 30px;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 0 20px rgba(0,0,0,0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            border-bottom: 3px solid #2c3e50;\n" +
                "            padding-bottom: 20px;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        .logo {\n" +
                "            font-size: 28px;\n" +
                "            font-weight: bold;\n" +
                "            color: #2c3e50;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .titulo-reporte {\n" +
                "            font-size: 24px;\n" +
                "            color: #e74c3c;\n" +
                "            font-weight: bold;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .info-section {\n" +
                "            margin-bottom: 25px;\n" +
                "            padding: 15px;\n" +
                "            background: #ecf0f1;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .info-title {\n" +
                "            font-weight: bold;\n" +
                "            color: #2c3e50;\n" +
                "            margin-bottom: 10px;\n" +
                "            font-size: 18px;\n" +
                "        }\n" +
                "        .info-content {\n" +
                "            padding: 10px;\n" +
                "            background: white;\n" +
                "            border-radius: 5px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .total-section {\n" +
                "            text-align: center;\n" +
                "            margin-top: 30px;\n" +
                "            padding: 30px;\n" +
                "            background: #2c3e50;\n" +
                "            color: white;\n" +
                "            border-radius: 10px;\n" +
                "        }\n" +
                "        .total-amount {\n" +
                "            font-size: 32px;\n" +
                "            font-weight: bold;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .dian-logo {\n" +
                "            text-align: center;\n" +
                "            margin: 20px 0;\n" +
                "            color: #e74c3c;\n" +
                "            font-weight: bold;\n" +
                "            font-size: 20px;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            margin-top: 30px;\n" +
                "            padding-top: 20px;\n" +
                "            border-top: 2px solid #bdc3c7;\n" +
                "            color: #7f8c8d;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .legal-notice {\n" +
                "            background: #fff3cd;\n" +
                "            border: 1px solid #ffeaa7;\n" +
                "            border-radius: 5px;\n" +
                "            padding: 15px;\n" +
                "            margin: 20px 0;\n" +
                "            font-size: 12px;\n" +
                "            color: #856404;\n" +
                "        }\n" +
                "        @media print {\n" +
                "            body { background: white; }\n" +
                "            .reporte-container { box-shadow: none; }\n" +
                "            .footer { display: none; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"reporte-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">ELECTROCOLOMBIANO</div>\n" +
                "            <div class=\"titulo-reporte\">REPORTE DIAN - DECLARACIÓN DE IVA</div>\n" +
                "            <div>Fecha de generación: " + fechaGeneracion + "</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"dian-logo\">\n" +
                "            DIRECCIÓN DE IMPUESTOS Y ADUANAS NACIONALES - DIAN\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"info-section\">\n" +
                "            <div class=\"info-title\">INFORMACIÓN DEL PERÍODO</div>\n" +
                "            <div class=\"info-content\">\n" +
                "                <strong>Año:</strong> " + year + "<br>\n" +
                "                <strong>Trimestre:</strong> " + quarter + " (" + periodo + ")<br>\n" +
                "                <strong>Rango de fechas:</strong> " + rangoFechas + "<br>\n" +
                "                <strong>Empresa:</strong> ELECTROCOLOMBIANO S.A.S.<br>\n" +
                "                <strong>NIT:</strong> 900.123.456-7\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"info-section\">\n" +
                "            <div class=\"info-title\">RESUMEN DE IVA RECAUDADO</div>\n" +
                "            <div class=\"info-content\">\n" +
                "                Este reporte presenta el total del Impuesto al Valor Agregado (IVA) recaudado \n" +
                "                durante el trimestre fiscal seleccionado, calculado sobre las ventas realizadas \n" +
                "                en el período.\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"total-section\">\n" +
                "            <div style=\"font-size: 18px; margin-bottom: 10px;\">TOTAL IVA RECAUDADO</div>\n" +
                "            <div class=\"total-amount\">$" + String.format("%,.2f", totalIVA) + "</div>\n" +
                "            <div style=\"font-size: 14px;\">\n" +
                "                Pesos Colombianos\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"legal-notice\">\n" +
                "            <strong>NOTA LEGAL:</strong> Este documento es un reporte interno generado por el sistema \n" +
                "            de ELECTROCOLOMBIANO. Para la presentación oficial ante la DIAN, utilice los formatos \n" +
                "            y procedimientos establecidos por la entidad. Este reporte tiene fines informativos \n" +
                "            y de control interno.\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>ELECTROCOLOMBIANO - Sistema de Gestión de Ventas</p>\n" +
                "            <p>Reporte generado automáticamente - " + fechaGeneracion + "</p>\n" +
                "            <p><small>Este documento es una representación digital del reporte de IVA</small></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        // Auto-impresión opcional\n" +
                "        window.onload = function() { \n" +
                "            setTimeout(function() { window.print(); }, 1000);\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private String obtenerPeriodoTrimestre(int quarter) {
        switch (quarter) {
            case 1: return "Enero - Marzo";
            case 2: return "Abril - Junio";
            case 3: return "Julio - Septiembre";
            case 4: return "Octubre - Diciembre";
            default: return "Período no válido";
        }
    }

    private String obtenerRangoFechas(int year, int quarter) {
        switch (quarter) {
            case 1: return "01/01/" + year + " - 31/03/" + year;
            case 2: return "01/04/" + year + " - 30/06/" + year;
            case 3: return "01/07/" + year + " - 30/09/" + year;
            case 4: return "01/10/" + year + " - 31/12/" + year;
            default: return "Rango no válido";
        }
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
        assert combo_trimestre != null : "fx:id=\"combo_trimestre\" was not injected: check your FXML file 'DIANreport.fxml'.";
        assert combo_year != null : "fx:id=\"combo_year\" was not injected: check your FXML file 'DIANreport.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'DIANreport.fxml'.";
        combo_trimestre.getItems().setAll(1,2,3,4);
        combo_year.getItems().setAll(2025,2024,2023,2022,2021,2020);

        // Establecer valores por defecto
        combo_year.setValue(2024);
        combo_trimestre.setValue(1);
    }
}