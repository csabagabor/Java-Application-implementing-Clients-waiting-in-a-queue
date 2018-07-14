package model;

import control.Server;

import java.util.List;

/**
 * Created by Csabi on 3/25/2018.
 */
public class ConcreteStrategyQueue implements Strategy {

	public boolean addTask(List<Server> servers, Task t, int maxTasksPerServer) {
		// we have to choose the right server where we will insert the new task
		// for this strategy, find the server which processes the least amount of tasks
		int min = servers.get(0).getTasks().length;
		int index = 0;
		for (int i = 0; i < servers.size(); i++) {
			Server server = servers.get(i);
			if (server.getTasks().length < min) {
				min = server.getTasks().length;
				index = i;
			}
		}

		// we insert the task into the chosen server
		if (servers.get(index).getTasks().length < maxTasksPerServer) {
			servers.get(index).addTask(t);
			return true;
		}
		return false;

	}

}
