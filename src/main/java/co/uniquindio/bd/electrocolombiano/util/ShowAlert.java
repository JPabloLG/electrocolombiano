package co.uniquindio.bd.electrocolombiano.util;

import javafx.scene.control.Alert;

public class ShowAlert {

    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
