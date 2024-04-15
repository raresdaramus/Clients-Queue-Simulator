package org.example.BusinessLogic.Strategy;

import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;

/**
 * Concrete strategy that allocates tasks to the server with the fewest number of tasks queued.
 */
public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task) {
        Server leastBusyServer = servers.get(0);
        for (Server server : servers) {
            if (server.getTasks().length < leastBusyServer.getTasks().length) {
                leastBusyServer = server;
            }
        }
        leastBusyServer.addTask(task);
    }
}
