module sv.edu.udb.desafio2_ped {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens sv.edu.udb.desafio2_ped to javafx.fxml;
    exports sv.edu.udb.desafio2_ped;
    opens sv.edu.udb.desafio2_ped.controller to javafx.fxml;
    exports sv.edu.udb.desafio2_ped.controller;
}