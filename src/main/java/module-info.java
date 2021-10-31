module org.verigo.course_project_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires unirest.java;

    requires gson;
    requires java.sql;

    opens org.verigo.course_project_client to javafx.fxml;

    opens org.verigo.course_project_client.models to gson;

    exports org.verigo.course_project_client;
}