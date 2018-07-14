package model;

import control.Server;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

	public boolean addTask(List<Server> servers, Task t, int maxTasksPerServer) {
		// we have to choose the right server where we will insert the new task
		// for this strategy, find the server where the waiting time is the least
		int min = servers.get(0).getWaitingPeriod();
		int index = 0;
		for (int i = 0; i < servers.size(); i++) {
			Server server = servers.get(i);
			if (server.getWaitingPeriod() < min) {
				min = server.getWaitingPeriod();
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
