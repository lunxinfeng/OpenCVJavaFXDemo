package com.lxf.app;

import com.lxf.ui.HomeController;
import com.lxf.ui.HomePage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        HomePage homePage = new HomePage(new HomeController());
        Scene scene = new Scene(homePage);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1920 * 0.8);
        primaryStage.setHeight(1080 * 0.8);
        primaryStage.show();
    }
}
