package control;

import model.ConcreteStrategyQueue;
import model.ConcreteStrategyTime;
import model.SelectionPolicy;
import model.Strategy;
import model.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
	private List<Server> servers = new LinkedList<Server>();
	private int maxNoServers;
	private int maxTasksPerServer = 0;
	private Strategy strategy = new ConcreteStrategyQueue();
	private AtomicInteger waitingTime = new AtomicInteger(0);
	private SimulatorManager manager;

	public Scheduler(int maxNoServers, int maxTasksPerServer, SimulatorManager manager) {
		// we use all the servers available
		this.manager = manager;
		for (int i = 0; i < maxNoServers; i++) {
			Server server = new Server(maxTasksPerServer, i, this);
			this.servers.add(server);
			Thread th = new Thread(server);
			th.start();
		}
	}

	public int getCurrentTime() {
		return this.manager.getCurrentTime();
	}

	public void incrementWaitingTime(int value) {
		this.waitingTime.addAndGet(value);
	}

	public int getWaitingTime() {
		return waitingTime.intValue();
	}

	public void changeStrategy(SelectionPolicy policy) {
		if (policy == SelectionPolicy.SHORTEST_QUEUE) {
			strategy = new ConcreteStrategyQueue();
		}
		if (policy == SelectionPolicy.SHORTEST_TIME) {
			strategy = new ConcreteStrategyTime();
		}

	}

	public void stopServers() {
		for (int i = 0; i < this.servers.size(); i++) {
			this.servers.get(i).stop();
		}
	}

	public boolean dispatchTask(Task t) {
		if (this.strategy.addTask(this.servers, t, maxTasksPerServer) == true)
			return true;
		return false;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public int getMaxNoServers() {
		return maxNoServers;
	}

	public void setMaxNoServers(int maxNoServers) {
		this.maxNoServers = maxNoServers;
	}

	public int getMaxTasksPerServer() {
		return maxTasksPerServer;
	}

	public void setMaxTasksPerServer(int maxTasksPerServer) {
		this.maxTasksPerServer = maxTasksPerServer;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
}
