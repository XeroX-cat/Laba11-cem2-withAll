module ru.itmo.sofi.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens ru.itmo.sofi.demo to javafx.fxml;
//    opens ru.itmo.sofi.essence.instrument to javafx.base;
//    opens ru.itmo.sofi.essence.booking to javafx.base;
//    opens ru.itmo.sofi.essence.checkout to javafx.base;
    opens ru.itmo.sofi.essence.instrument to javafx.base, com.fasterxml.jackson.databind;
    opens ru.itmo.sofi.essence.booking to javafx.base, com.fasterxml.jackson.databind;
    opens ru.itmo.sofi.essence.checkout to javafx.base, com.fasterxml.jackson.databind;

    exports ru.itmo.sofi.demo;
}