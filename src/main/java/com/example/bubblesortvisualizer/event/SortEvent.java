package com.example.bubblesortvisualizer.event;

public class SortEvent {
    private final int index1;
    private final int index2;
    private boolean swap;

    public SortEvent(int index1, int index2) {
        this.index1 = index1;
        this.index2 = index2;
    }

    public int getIndex1() {
        return index1;
    }

    public int getIndex2() {
        return index2;
    }

    public void setSwap(boolean swap) {
        this.swap = swap;
    }

    public boolean isSwap() {
        return swap;
    }

    @Override
    public String toString() {
        return "SortEvent{" +
                "index1=" + index1 +
                ", index2=" + index2 +
                ", swap=" + swap +
                '}';
    }
}
