package org.example.Model;

import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private int id;
    private int arrivalTime;
    private AtomicInteger serviceTime;
    private volatile boolean isQueued; // Indicates if the task has been added to a queue
    private int startServiceTime;

    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = new AtomicInteger(serviceTime);
        this.isQueued = false;
        this.startServiceTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getServiceTime() {
        return serviceTime.get();
    }

    public void decreaseServiceTime() {
        this.serviceTime.getAndDecrement(); // Thread-safe decrement
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public boolean isQueued() {
        return isQueued;
    }

    public void setQueued(boolean queued) {
        this.isQueued = queued;
    }
    public int getStartServiceTime() {
        return startServiceTime;
    }

    public void setStartServiceTime(int startServiceTime) {
        this.startServiceTime = startServiceTime;
    }


    @Override
    public String toString() {
        return String.format("(%d,%d,%d)", id, arrivalTime, serviceTime.get());
    }
}
