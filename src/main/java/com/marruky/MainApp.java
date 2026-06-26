package com.marruky;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
