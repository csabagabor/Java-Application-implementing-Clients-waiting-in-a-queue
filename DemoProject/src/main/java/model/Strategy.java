package model;

import control.Server;

import java.util.List;

public interface Strategy {
	public boolean addTask(List<Server> servers, Task t, int maxTasksPerServer);
}
