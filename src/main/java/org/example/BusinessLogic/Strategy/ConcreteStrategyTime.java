package org.example.BusinessLogic.Strategy;

import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;

/**
 * Concrete strategy that allocates tasks to the server with the shortest current waiting time.
 */
public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task) {
        if(servers == null || servers.isEmpty())
        {
            throw new IllegalStateException("No servers available!");
        }
        Server shortestTimeServer = servers.get(0);
        for (Server server : servers) {
            if (server.getWaitingPeriod() < shortestTimeServer.getWaitingPeriod()) {
                shortestTimeServer = server;
            }
        }
        shortestTimeServer.addTask(task);
    }
}
