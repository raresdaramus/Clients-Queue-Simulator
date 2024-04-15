package org.example.BusinessLogic;

import org.example.BusinessLogic.Strategy.ConcreteStrategyQueue;
import org.example.BusinessLogic.Strategy.ConcreteStrategyTime;
import org.example.BusinessLogic.Strategy.SelectionPolicy;
import org.example.BusinessLogic.Strategy.Strategy;
import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private AtomicInteger serversWaitingTime;

    private AtomicInteger maxClientsSeen = new AtomicInteger(0);
    private AtomicInteger maxHour = new AtomicInteger(0);


    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.serversWaitingTime = new AtomicInteger();
        this.servers = new ArrayList<>();

        for(int i = 0; i < maxNoServers; i++)
        {
            Server server = new Server(i+1);
            servers.add(server);
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }

    public synchronized void changeStrategy(SelectionPolicy policy)
    {
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }

        if(policy == SelectionPolicy.SHORTEST_TIME)
        {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task task) {
        if(servers == null || servers.isEmpty()) {
            System.out.println("No servers available to dispatch the task!");
            return;
        }
        strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public AtomicInteger getWaitingTime()
    {
        AtomicInteger total = new AtomicInteger(0);
        for (Server server : servers) {
            total.addAndGet(server.getWaitingPeriod());
        }
        return total;
    }
    public boolean hasActiveTasks() {
        // Check if any server still has tasks using the new isQueueEmpty method.
        return servers.stream().anyMatch(server -> !server.isQueueEmpty());
    }


    public void updateClientMetrics() {
        int currentClients = 0;
        for (Server server : servers) {
            currentClients += server.getQueueLength();  // Presupunem că există o metodă getQueueLength() în Server
        }
        if (currentClients > maxClientsSeen.get()) {
            maxClientsSeen.set(currentClients);
            maxHour.set(SimulationManager.getCurrentTime());  // Presupunem că există o metodă statică getCurrentTime() în SimulationManager
        }

    }

    public int getMaxClientsSeen() {
        return maxClientsSeen.get();
    }

    public int getMaxHour() {
        return maxHour.get();
    }


    public void stopAllServers() {
        for (Server server : servers) {
            server.stopServer();  // Stop each server.
        }
    }

    public String displayServers() {
        StringBuilder builder = new StringBuilder();
        for (Server server : servers) {
            builder.append(server.toString()).append("\n");
        }
        return builder.toString();
    }
}
