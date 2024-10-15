module com.example.bubblesort {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.bubblesortvisualizer;
    opens com.example.bubblesortvisualizer to javafx.fxml;
    exports com.example.bubblesortvisualizer.algorithm;
    opens com.example.bubblesortvisualizer.algorithm to javafx.fxml;
    exports com.example.bubblesortvisualizer.controller;
    opens com.example.bubblesortvisualizer.controller to javafx.fxml;
    exports com.example.bubblesortvisualizer.enumeration;
    opens com.example.bubblesortvisualizer.enumeration to javafx.fxml;
    exports com.example.bubblesortvisualizer.event;
    opens com.example.bubblesortvisualizer.event to javafx.fxml;
}