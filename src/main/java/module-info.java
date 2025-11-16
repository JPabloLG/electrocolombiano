module co.uniquindio.bd.electrocolombiano {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.uniquindio.bd.electrocolombiano to javafx.fxml;
    exports co.uniquindio.bd.electrocolombiano;
}