module com.senai.javafx.brasabrasil {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.senai.javafx.brasabrasil to javafx.fxml;
    exports com.senai.javafx.brasabrasil;
}
