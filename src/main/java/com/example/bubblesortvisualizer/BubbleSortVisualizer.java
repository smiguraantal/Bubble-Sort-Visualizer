package com.example.bubblesortvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class BubbleSortVisualizer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BubbleSortVisualizer.class.getResource("bubble-sort.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bubble Sort Visualizer");
        stage.setResizable(false);
        scene.setFill(Color.BEIGE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}