package co.uniquindio.bd.electrocolombiano.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import co.uniquindio.bd.electrocolombiano.App;
import co.uniquindio.bd.electrocolombiano.model.ElectronicStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private Label txt_numberSale;

    @FXML
    private Label txt_paymentType;

    @FXML
    private Label txt_total;

    private final ElectronicStore store = ElectronicStore.getSingleton();

    @FXML
    void back_btn(ActionEvent event) throws IOException {
        App.setRoot("createSale", "ELECTROCOLOMBIANO -Crear Venta-");
    }

    @FXML
    void finishProcess_btn(ActionEvent event) throws IOException {
        store.setSale(null);
        App.setRoot("principal", "ELECTROCOLOMBIANO -Panel Principal de Gestión-");
    }

    @FXML
    void generate_pdf_btn(ActionEvent event) {
        try {
            generarFacturaEnNavegador();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al generar la factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarFacturaEnNavegador() {
        try {
            // Crear contenido HTML de la factura
            String htmlContent = crearContenidoFactura();

            // Crear un archivo temporal
            java.io.File tempFile = java.io.File.createTempFile("factura_" + store.getSale().getId() + "_", ".html");

            // Escribir el contenido HTML
            java.io.FileWriter writer = new java.io.FileWriter(tempFile);
            writer.write(htmlContent);
            writer.close();

            // Usar java.awt.Desktop para abrir en el navegador (más confiable)
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(tempFile.toURI());
                    mostrarAlerta("Éxito", "Factura abierta en el navegador");

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
                            10000 // 10 segundos
                    );
                    return;
                }
            }

            // Si Desktop no funciona, intentar con el método antiguo
            abrirConRuntime(tempFile);

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir en el navegador: " + e.getMessage());
        }
    }

    private void abrirConRuntime(java.io.File tempFile) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("win")) {
                // Windows - usar el comando correcto
                String cmd = "cmd /c start \"\" \"" + tempFile.getAbsolutePath() + "\"";
                rt.exec(cmd);
            } else if (os.contains("mac")) {
                // Mac
                rt.exec(new String[]{"open", tempFile.getAbsolutePath()});
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux/Unix
                rt.exec(new String[]{"xdg-open", tempFile.getAbsolutePath()});
            } else {
                // Sistema operativo no reconocido
                rt.exec(new String[]{"open", tempFile.getAbsolutePath()});
            }

            mostrarAlerta("Éxito", "Factura abierta en el navegador");

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
                    10000 // 10 segundos
            );

        } catch (Exception e) {
            // Si todo falla, mostrar el contenido en un diálogo
            mostrarContenidoEnDialogo();
        }
    }

    private void mostrarContenidoEnDialogo() {
        try {
            String htmlContent = crearContenidoFactura();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Factura - ELECTROCOLOMBIANO");
            alert.setHeaderText("Factura Generada");
            alert.setContentText("La factura se ha generado correctamente.\n\n" +
                    "ID de Venta: " + store.getSale().getId() + "\n" +
                    "Cliente: " + store.getSale().getCustomer().getUserName() + "\n" +
                    "Total: $" + String.format("%,.2f", store.getSale().getTotalPrice()) + "\n\n" +
                    "Puede imprimir esta información o tomar captura de pantalla.");
            alert.getDialogPane().setPrefSize(400, 300);
            alert.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar la factura: " + e.getMessage());
        }
    }

    private String crearContenidoFactura() {
        String tipoPago = store.getSale().getIsCredit() ? "Crédito" : "Contado";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Factura - ELECTROCOLOMBIANO</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 20px;\n" +
                "            background-color: #f5f5f5;\n" +
                "        }\n" +
                "        .factura-container {\n" +
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
                "        .numero-factura {\n" +
                "            font-size: 24px;\n" +
                "            color: #e74c3c;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .info-section {\n" +
                "            margin-bottom: 25px;\n" +
                "        }\n" +
                "        .info-title {\n" +
                "            font-weight: bold;\n" +
                "            color: #2c3e50;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        .info-content {\n" +
                "            padding: 10px;\n" +
                "            background: #ecf0f1;\n" +
                "            border-radius: 5px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .productos-table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .productos-table th {\n" +
                "            background: #34495e;\n" +
                "            color: white;\n" +
                "            padding: 12px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .productos-table td {\n" +
                "            padding: 10px;\n" +
                "            border-bottom: 1px solid #ddd;\n" +
                "        }\n" +
                "        .productos-table tr:nth-child(even) {\n" +
                "            background: #f8f9fa;\n" +
                "        }\n" +
                "        .total-section {\n" +
                "            text-align: right;\n" +
                "            margin-top: 20px;\n" +
                "            padding: 20px;\n" +
                "            background: #2c3e50;\n" +
                "            color: white;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .total-amount {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            margin-top: 30px;\n" +
                "            padding-top: 20px;\n" +
                "            border-top: 2px solid #bdc3c7;\n" +
                "            color: #7f8c8d;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        @media print {\n" +
                "            body { background: white; }\n" +
                "            .factura-container { box-shadow: none; }\n" +
                "            .footer { display: none; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"factura-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">ELECTROCOLOMBIANO</div>\n" +
                "            <div class=\"numero-factura\">FACTURA #" + store.getSale().getId() + "</div>\n" +
                "            <div>Fecha: " + fecha + "</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"info-section\">\n" +
                "            <div class=\"info-title\">INFORMACIÓN DEL CLIENTE</div>\n" +
                "            <div class=\"info-content\">\n" +
                "                <strong>Nombre:</strong> " + store.getSale().getCustomer().getUserName() + "<br>\n" +
                "                <strong>Cédula:</strong> " + store.getSale().getCustomer().getCedula() + "<br>\n" +
                "                <strong>Tipo de Pago:</strong> " + tipoPago + "\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"info-section\">\n" +
                "            <div class=\"info-title\">INFORMACIÓN DEL EMPLEADO</div>\n" +
                "            <div class=\"info-content\">\n" +
                "                <strong>Vendedor:</strong> " + store.getSale().getEmployee().getFullName() + "<br>\n" +
                "                <strong>Cédula:</strong> " + store.getSale().getEmployee().getCedula() + "\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"info-section\">\n" +
                "            <div class=\"info-title\">PRODUCTOS ADQUIRIDOS</div>\n" +
                "            <table class=\"productos-table\">\n" +
                "                <thead>\n" +
                "                    <tr>\n" +
                "                        <th>Producto</th>\n" +
                "                        <th>Cantidad</th>\n" +
                "                        <th>Precio Unitario</th>\n" +
                "                        <th>Subtotal</th>\n" +
                "                    </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                generarFilasProductos() +
                "                </tbody>\n" +
                "            </table>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"total-section\">\n" +
                "            <div>SUBTOTAL: $" + String.format("%,.2f", store.getSale().getSubtotal()) + "</div>\n" +
                "            <div class=\"total-amount\">TOTAL: $" + String.format("%,.2f", store.getSale().getTotalPrice()) + "</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>¡Gracias por su compra!</p>\n" +
                "            <p>ELECTROCOLOMBIANO - Sistema de Gestión de Ventas</p>\n" +
                "            <p>Este documento es una representación digital de la factura de venta</p>\n" +
                "            <p><small>Generado automáticamente - " + fecha + "</small></p>\n" +
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

    private String generarFilasProductos() {
        StringBuilder filas = new StringBuilder();
        if (store.getSale().getProducts() != null) {
            store.getSale().getProducts().forEach(producto -> {
                double subtotalProducto = producto.getUnitPrice().doubleValue() * producto.getQuantity();
                filas.append("<tr>")
                        .append("<td>").append(producto.getName()).append("</td>")
                        .append("<td>").append(producto.getQuantity()).append("</td>")
                        .append("<td>$").append(String.format("%,.2f", producto.getUnitPrice())).append("</td>")
                        .append("<td>$").append(String.format("%,.2f", subtotalProducto)).append("</td>")
                        .append("</tr>\n");
            });
        } else {
            filas.append("<tr><td colspan='4' style='text-align: center;'>No hay productos registrados</td></tr>");
        }

        return filas.toString();
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
        assert txt_cedula != null : "fx:id=\"txt_cedula\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_header_currentUser != null : "fx:id=\"txt_header_currentUser\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_name != null : "fx:id=\"txt_name\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_paymentType != null : "fx:id=\"txt_paymentType\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_total != null : "fx:id=\"txt_total\" was not injected: check your FXML file 'finishSale.fxml'.";
        assert txt_numberSale != null : "fx:id=\"txt_numberSale\" was not injected: check your FXML file 'finishSale.fxml'.";

        txt_header_currentUser.setText(store.getCurrentUser().getFullName());
        txt_cedula.setText(store.getSale().getCustomer().getCedula());
        txt_name.setText(store.getSale().getCustomer().getFullName());
        txt_total.setText("$" + store.getSale().getTotalPrice().toString());
        txt_numberSale.setText(store.getSale().getId());
        if(store.getSale().getIsCredit() == true){
            txt_paymentType.setText("Crédito");
        }else{
            txt_paymentType.setText("Contado");
        }
    }
}