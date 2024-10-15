package com.example.bubblesortvisualizer.algorithm;

import com.example.bubblesortvisualizer.event.SortEvent;

import java.util.ArrayList;
import java.util.List;

public class BubbleSortAlgorithm {

    private final List<SortEvent> events = new ArrayList<>();

    public void sort(int[] array) {
        int n = array.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                SortEvent event = new SortEvent(j, j + 1);
                events.add(event);

                if (array[j] > array[j + 1]) {
                    event.setSwap(true);
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    public List<SortEvent> getEvents() {
        return events;
    }
}