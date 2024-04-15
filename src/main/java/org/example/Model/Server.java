package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private int id;
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile boolean running = true;

    public Server(int id) {
        this.id = id;
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public synchronized void addTask(Task newTask) {
        boolean isAdded = tasks.add(newTask);
       // System.out.println("Task " + newTask.getId() + " added!");
        if (!isAdded) {
            System.out.println("Failed to add element to the queue. Queue may be full.");
        } else {
            newTask.setQueued(true);
            waitingPeriod.addAndGet(newTask.getServiceTime());
        }
    }



    @Override
    public void run() {
        while (running) {
            try {
                Task currentTask = null;
                    if (!tasks.isEmpty()) {
                        currentTask = tasks.peek();
                    }
                if (currentTask != null) {
                   // System.out.println("Current task: " + currentTask + " on server: " + this.id);

                    int totalServiceTime = currentTask.getServiceTime();
                    for (int i = 0; i < totalServiceTime; i++) {
                        Thread.sleep(1000); // Sleep for one second
                        currentTask.decreaseServiceTime(); // Decrement service time safely
                    }

                       // waitingPeriod.addAndGet(-currentTask.getServiceTime());
                        tasks.remove();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public int getId()
    {
        return this.id;
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }
    public boolean isQueueEmpty() {
        return tasks.isEmpty();
    }

    public void stopServer() {
        this.running = false;
    }

    public int getQueueLength() {
        return tasks.size();  // Returnează numărul de task-uri din coadă
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Queue ").append(id).append(" :");
        if(tasks.isEmpty())
        {
            builder.append("closed");
        }
        else {
            for (Task task : tasks) {
                builder.append(task).append(", ");
            }
            if (!tasks.isEmpty()) {
                builder.setLength(builder.length() - 2); // Remove the last ", "
            }
        }

        return builder.toString();
    }

}
