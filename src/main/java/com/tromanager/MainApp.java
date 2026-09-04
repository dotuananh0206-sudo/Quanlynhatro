package com.tromanager;

import com.tromanager.controller.DialogHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            if (!DialogHelper.showLoginDialog()) {
                Platform.exit();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tromanager/fxml/main_layout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1380, 850);
            scene.getStylesheets().add(getClass().getResource("/com/tromanager/css/styles.css").toExternalForm());

            primaryStage.setTitle("TroManager - Phần Mềm Quản Lý Phòng Trọ");
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(720);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
