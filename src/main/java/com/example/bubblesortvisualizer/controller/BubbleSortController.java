package com.example.bubblesortvisualizer.controller;

import com.example.bubblesortvisualizer.algorithm.BubbleSortAlgorithm;
import com.example.bubblesortvisualizer.enumeration.Direction;
import com.example.bubblesortvisualizer.event.SortEvent;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BubbleSortController {

    @FXML
    private HBox hbox;

    @FXML
    public Button sortButton;

    @FXML
    public Button shuffleButton;

    private static final Color ORIGINAL_COLOR = Color.DODGERBLUE;
    private static final Color COMPARE_COLOR = Color.GOLD;
    private static final Color FINISHED_COLOR = Color.GREEN;

    private static final double DELAY = 0.75;
    private static final double WAVE_DURATION = 0.1;
    private static final double JUMP_DURATION = 0.25;
    private static final double HORIZONTAL_TRANSLATE_DURATION = 0.4;
    private static final double VERTICAL_TRANSLATE_DURATION = 0.25;
    private static final double TIME_FACTOR = 0.5;

    private static final int DISTANCE_X = 150;
    private static final int DISTANCE_Y =75;
    private static final int JUMP_DISTANCE = 30;

    private static final int LAST_UNSORTED_VALUE = 2;

    private final List<Circle> circles = new ArrayList<>();
    private final List<Integer> values = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
    private int lastIndexOfOriginalColor = values.size() - 1;

    @FXML
    public void initialize() {
        initializeCircles();
        shuffleChildren();
    }

    private void initializeCircles() {
        for (Node child : hbox.getChildren()) {
            StackPane stackPane = (StackPane) child;
            Circle circle = (Circle) stackPane.getChildren().get(0);
            circle.setUserData(hbox.getChildren().indexOf(child) + 1);
            circles.add(circle);
        }
    }

    private void shuffleChildren() {
        Collections.shuffle(values);
        lastIndexOfOriginalColor = values.size() - 1;

        for (int i = 0; i < hbox.getChildren().size(); i++) {
            StackPane stackPane = (StackPane) hbox.getChildren().get(i);
            Circle circle = (Circle) stackPane.getChildren().get(0);

            circle.setUserData(values.get(i));
            originalCircleColor(i);

            Text text = (Text) stackPane.getChildren().get(1);
            text.setText(circle.getUserData().toString());
        }
    }

    private void startSorting() {
        BubbleSortAlgorithm bubbleSortAlgorithm = new BubbleSortAlgorithm();
        int[] values = getCircleValues();
        bubbleSortAlgorithm.sort(values);
        List<SortEvent> sortEvents = bubbleSortAlgorithm.getEvents();

        animateSorting(sortEvents);
    }

    private int[] getCircleValues() {
        int[] values = new int[hbox.getChildren().size()];
        for (int i = 0; i < hbox.getChildren().size(); i++) {
            StackPane stackPane = (StackPane) hbox.getChildren().get(i);
            Circle circle = (Circle) stackPane.getChildren().get(0);
            values[i] = (int) circle.getUserData();
        }
        return values;
    }

    private void animateSorting(List<SortEvent> sortEvents) {
        animateNextEvent(sortEvents, 0);
    }

    private void animateNextEvent(List<SortEvent> sortEvents, int index) {
        if (index >= sortEvents.size()) {
            return;
        }

        SortEvent sortEvent = sortEvents.get(index);
        int index1 = sortEvent.getIndex1();
        int index2 = sortEvent.getIndex2();

        compareCircleColor(index1);
        compareCircleColor(index2);

        PauseTransition pause = createPause(DELAY * TIME_FACTOR, event -> {
            if (!sortEvent.isSwap()) {
                jumpNumbers(sortEvents, sortEvent, index);
            } else {
                swapCircles(index1, index2, sortEvents, index);
            }
        });
        pause.play();
    }

    private void jumpNumbers(List<SortEvent> sortEvents, SortEvent sortEvent, int currentEventIndex) {
        int index1 = sortEvent.getIndex1();
        int index2 = sortEvent.getIndex2();

        jumpNumber(circles.get(index1));
        jumpNumber(circles.get(index2));

        PauseTransition pause = new PauseTransition(Duration.seconds(DELAY * TIME_FACTOR * 2));
        afterAnimation(pause, sortEvents, index1, index2, currentEventIndex);

        pause.play();
    }

    private void jumpNumber(Circle circle) {
        StackPane stackPane = (StackPane) circle.getParent();
        Text text = (Text) stackPane.getChildren().get(1);

        jumpNode(circle);
        jumpNode(text);
    }

    private void jumpNode(Node node) {
        TranslateTransition jump = new TranslateTransition(Duration.seconds(JUMP_DURATION * TIME_FACTOR), node);
        jump.setByY(-JUMP_DISTANCE);
        jump.setCycleCount(2);
        jump.setAutoReverse(true);
        jump.play();
    }

    private void swapCircles(int index1, int index2, List<SortEvent> sortEvents, int currentEventIndex) {
        StackPane stackPane1 = (StackPane) hbox.getChildren().get(index1);
        StackPane stackPane2 = (StackPane) hbox.getChildren().get(index2);

        Circle circle1 = (Circle) stackPane1.getChildren().get(0);
        Circle circle2 = (Circle) stackPane2.getChildren().get(0);

        Text text1 = (Text) stackPane1.getChildren().get(1);
        Text text2 = (Text) stackPane2.getChildren().get(1);

        ParallelTransition swapTransition = new ParallelTransition(
                createUShapeTransition(circle1),
                createReverseUShapeTransition(circle2),
                createUShapeTransition(text1),
                createReverseUShapeTransition(text2));

        swapTransition.setOnFinished(event -> {
            resetNodePosition(circle1, circle2, text1, text2);

            swapUserDataAndUpdateText(circle1, circle2, text1, text2);

            PauseTransition afterAnimationPause = new PauseTransition(Duration.seconds(DELAY * TIME_FACTOR));
            afterAnimation(afterAnimationPause, sortEvents, index1, index2, currentEventIndex);

            afterAnimationPause.play();
        });

        swapTransition.play();
    }

    SequentialTransition createUShapeTransition(Node node) {
        TranslateTransition moveUp = createTransition(node, 0, -DISTANCE_Y, Direction.VERTICAL);
        TranslateTransition moveRight = createTransition(node, DISTANCE_X, 0, Direction.HORIZONTAL);
        TranslateTransition moveDown = createTransition(node, 0, DISTANCE_Y, Direction.VERTICAL);
        return new SequentialTransition(moveUp, moveRight, moveDown);
    }

    SequentialTransition createReverseUShapeTransition(Node node) {
        TranslateTransition moveDown = createTransition(node, 0, DISTANCE_Y, Direction.VERTICAL);
        TranslateTransition moveLeft = createTransition(node, -DISTANCE_X, 0, Direction.HORIZONTAL);
        TranslateTransition moveUp = createTransition(node, 0, -DISTANCE_Y, Direction.VERTICAL);
        return new SequentialTransition(moveDown, moveLeft, moveUp);
    }

    private TranslateTransition createTransition(Node node, double byX, double byY, Direction direction) {
        double duration = direction == Direction.VERTICAL ? VERTICAL_TRANSLATE_DURATION * TIME_FACTOR : HORIZONTAL_TRANSLATE_DURATION * TIME_FACTOR;

        TranslateTransition transition = new TranslateTransition(Duration.seconds(duration), node);
        transition.setByX(byX);
        transition.setByY(byY);
        return transition;
    }

    private void resetNodePosition(Node... nodes) {
        for (Node node : nodes) {
            node.setTranslateX(0);
            node.setTranslateY(0);
        }
    }

    private void swapUserDataAndUpdateText(Circle circle1, Circle circle2, Text text1, Text text2) {
        Object tempUserData = circle1.getUserData();

        circle1.setUserData(circle2.getUserData());
        circle2.setUserData(tempUserData);

        text1.setText(circle1.getUserData().toString());
        text2.setText(circle2.getUserData().toString());
    }

    private void afterAnimation(PauseTransition pause, List<SortEvent> sortEvents, int index1, int index2, int currentEventIndex) {
        pause.setOnFinished(event -> {
            originalCircleColor(index1);
            originalCircleColor(index2);

            checkLastElement(index1, index2);

            PauseTransition afterColorReset = createPause(DELAY * TIME_FACTOR, e -> {
                animateNextEvent(sortEvents, currentEventIndex + 1);
            });

            afterColorReset.play();
        });
    }

    private void checkLastElement(int index1, int index2) {
        Circle lastCircleInOriginalColor = circles.get(lastIndexOfOriginalColor);
        int value = (int) lastCircleInOriginalColor.getUserData();

        if (index2 == lastIndexOfOriginalColor) {
            sortedCircleColor(index2);
            lastIndexOfOriginalColor--;

            if (value == LAST_UNSORTED_VALUE) {
                sortedCircleColor(index1);

                PauseTransition afterSorting = createPause(DELAY * TIME_FACTOR, event -> {
                    animateCompletion();
                });
                afterSorting.play();
            }
        }
    }

    private void animateCompletion() {
        SequentialTransition sequentialTransition = new SequentialTransition();

        for (int i = 0; i < hbox.getChildren().size(); i++) {
            StackPane stackPane = (StackPane) hbox.getChildren().get(i);
            Circle circle = (Circle) stackPane.getChildren().get(0);
            Text text = (Text) stackPane.getChildren().get(1);

            PauseTransition pause = new PauseTransition(Duration.seconds(WAVE_DURATION * TIME_FACTOR));
            jumpNode(circle);
            jumpNode(text);

            sequentialTransition.getChildren().addAll(pause);
        }

        sequentialTransition.setOnFinished(event -> {
            shuffleButton.setDisable(false);
        });

        sequentialTransition.play();
    }

    private PauseTransition createPause(double delay, EventHandler<ActionEvent> event) {
        PauseTransition pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(event);
        return pause;
    }

    private void originalCircleColor(int index) {
        setCircleColor(index, ORIGINAL_COLOR);
    }

    private void compareCircleColor(int index) {
        setCircleColor(index, COMPARE_COLOR);
    }

    private void sortedCircleColor(int index) {
        setCircleColor(index, FINISHED_COLOR);
    }

    private void setCircleColor(int index, Color color) {
        StackPane stackPane = (StackPane) hbox.getChildren().get(index);
        Circle currentCircle = (Circle) stackPane.getChildren().get(0);
        currentCircle.setFill(color);
    }

    @FXML
    public void onSortButtonClicked(ActionEvent actionEvent) {
        startSorting();
        sortButton.setDisable(true);
        shuffleButton.setDisable(true);
    }

    @FXML
    public void onShuffleButtonClicked(ActionEvent actionEvent) {
        shuffleChildren();
        sortButton.setDisable(false);
    }
}