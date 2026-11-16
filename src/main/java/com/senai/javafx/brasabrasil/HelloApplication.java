
package com.senai.javafx.brasabrasil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class    HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL caminhoFXML = HelloApplication.class.getResource(
                "/com/senai/javafx/brasabrasil/hello-view.fxml"
        );

        if (caminhoFXML == null) {
            throw new IOException(
                    "Não foi possível encontrar hello-view.fxml."
            );
        }

        FXMLLoader fxmlLoader = new FXMLLoader(caminhoFXML);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        stage.setTitle("Brasa Brasil");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setMaximized(true);
        stage.show();
    }
}
