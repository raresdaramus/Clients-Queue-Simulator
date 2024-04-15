package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;
import org.example.BusinessLogic.Strategy.SelectionPolicy;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Manages the simulation of a queue system.
 */
public class SimulationManager implements Runnable {
    private int timeLimit = 60; // Default value
    private static int currentTime = 0;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private SelectionPolicy selectionPolicy;
    private JTextArea outputArea;

    private Scheduler scheduler;
    private List<Task> generatedTasks; // To keep track of all tasks for statistical purposes
    private PrintStream fileOutput;
    private PrintStream originalSystemOut;
    private AtomicInteger totalWaitTime = new AtomicInteger(0);

    public SimulationManager(int time, int numberOfServers, int numberOfClients, int minArrival, int maxArrival,
                             int minService, int maxService, SelectionPolicy policy, JTextArea outputArea) {

        this.timeLimit = time;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.minArrivalTime = minArrival;
        this.maxArrivalTime = maxArrival;
        this.minProcessingTime = minService;
        this.maxProcessingTime = maxService;
        this.selectionPolicy = policy;
        this.scheduler = new Scheduler(numberOfServers, numberOfClients);
        scheduler.changeStrategy(selectionPolicy);
        this.outputArea = outputArea;
        this.generatedTasks = new ArrayList<>();
        generateNRandomTasks();

        setupOutputStream();
    }

    private void setupOutputStream() {
        try {
            // Create a FileOutputStream to write to the file "text.txt".
            // The boolean 'true' argument means the FileOutputStream is set for appending.
            FileOutputStream fos = new FileOutputStream("text.txt", true);

            // Create a PrintStream from the FileOutputStream.
            fileOutput = new PrintStream(new BufferedOutputStream(fos), true);

            // Redirect System.out to write directly to the fileOutput PrintStream.
            System.setOut(fileOutput);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to open file for output.");
            e.printStackTrace();
        }
    }

    public static int getCurrentTime() {
        return currentTime;  // Metoda statică ce returnează ora curentă
    }

    public void closeStreams() {
        if (fileOutput != null) {
            fileOutput.close();
        }
        System.setOut(originalSystemOut);
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        for (int i = 0; i < numberOfClients; i++) {
            int processingTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            Task task = new Task(i + 1, arrivalTime, processingTime);
            generatedTasks.add(task);
            Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
        }
    }

    @Override
    public void run() {
        try {
            int maxHour = 0;

            while (currentTime <= timeLimit && (scheduler.hasActiveTasks() || !generatedTasks.isEmpty())) {
                System.out.println("Time: " + currentTime);
                outputArea.append("Time: " + currentTime + "\n"); // This will not affect the file output

                synchronized (generatedTasks) {
                    Iterator<Task> taskIterator = generatedTasks.iterator();
                    while (taskIterator.hasNext()) {
                        Task task = taskIterator.next();
                        if (task.getArrivalTime() == currentTime) {
                            scheduler.dispatchTask(task);
                            taskIterator.remove();
                        }
                    }
                }

                System.out.println("Waiting list: " + generatedTasks.toString());
                System.out.println(scheduler.displayServers());

                outputArea.append("Waiting list: " + generatedTasks.toString() + "\n");
                outputArea.append(scheduler.displayServers() + "\n");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                scheduler.updateClientMetrics();  // Actualizează maximul de clienți văzuți și ora la care s-a întâmplat
                totalWaitTime.addAndGet(scheduler.getWaitingTime().get());
                currentTime++;
            }

            scheduler.stopAllServers();
            double averageWaitingTime = totalWaitTime.get() / (double) currentTime;
            double averageServiceTime = scheduler.getWaitingTime().get() / (double) numberOfClients;
            System.out.println("Average waiting time: " + averageWaitingTime);
            System.out.println("Simulation complete! Average service time: " + averageServiceTime);
            System.out.println("Max hour with most clients: " + scheduler.getMaxHour() + " with " + scheduler.getMaxClientsSeen() + " clients.");

            outputArea.append("Simulation complete! Average service time:" + averageServiceTime + "\n");
            outputArea.append("Average waiting time: " + averageWaitingTime + "\n");
            outputArea.append("Peak hour with most clients: " + scheduler.getMaxHour() + " with " + scheduler.getMaxClientsSeen()  + " clients.\n");
        } finally {
            if (fileOutput != null) {
                fileOutput.close();  // Ensure the file stream is closed properly.
            }
        }
    }
}
