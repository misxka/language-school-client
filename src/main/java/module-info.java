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
    requires java.dotenv;
    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;

    opens org.verigo.course_project_client to javafx.fxml;

    opens org.verigo.course_project_client.models to gson, javafx.base;

    exports org.verigo.course_project_client;
    exports org.verigo.course_project_client.controllers;
    opens org.verigo.course_project_client.controllers to javafx.fxml;
    exports org.verigo.course_project_client.controllers.teacher;
    opens org.verigo.course_project_client.controllers.teacher to javafx.fxml;
    exports org.verigo.course_project_client.controllers.student;
    opens org.verigo.course_project_client.controllers.student to javafx.fxml;
    exports org.verigo.course_project_client.models to com.fasterxml.jackson.databind;
}