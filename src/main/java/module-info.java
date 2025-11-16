module co.uniquindio.bd.electrocolombiano {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires java.management;
    requires javafx.graphics;

    opens co.uniquindio.bd.electrocolombiano to javafx.fxml;
    exports co.uniquindio.bd.electrocolombiano;

    opens co.uniquindio.bd.electrocolombiano.controller to javafx.fxml;
    exports co.uniquindio.bd.electrocolombiano.controller to javafx.fxml;
}