package control;

import model.ArrivalTimeComparator;
import model.SelectionPolicy;
import model.Task;
import view.SimulatorFrame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;

public class SimulatorManager implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(SimulatorManager.class.getName());
	public static int simulationSpeed = 1000;
	public static PrintWriter writer;
	private int timeLimit = 0;
	private int maxProcessingTime = 0;
	private int minProcessingTime = 0;
	private int numberOfServers = 0;
	private int numberOfClients = 0;
	private int minArrivingTime = 0;
	private int maxArrivingTime = 0;
	private int maxTasksPerServer = 0;
	private int peakHour = 0;
	private int peakHourClients = 0;
	private int currentTime = 0;
	private int averageServiceTime = 0;
	private int[] emptyQueueTime;
	public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
	private Scheduler scheduler;
	private SimulatorFrame frame;
	private List<Task> generatedTasks = new LinkedList<Task>();

	public int[] getEmptyQueueTime() {
		return emptyQueueTime;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public int getNumberOfClients() {
		return numberOfClients;
	}

	public int getAverageServiceTime() {
		return averageServiceTime;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public int getPeakHour() {
		return peakHour;
	}

	public int getAverageWaitingTime() {
		// number of clients is always greater than 0
		return this.scheduler.getWaitingTime() / this.numberOfClients;
	}

	public int getPeakHourClients() {
		return peakHourClients;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public SimulatorManager() {
		// LogManager.getLogManager().reset();
		try {
			writer = new PrintWriter("logs.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		frame = new SimulatorFrame(this);
	}

	/**
	 * Generates N random clients/tasks
	 */
	private void generateNRandomTasks() {
		this.averageServiceTime = 0;
		for (int i = 1; i <= this.numberOfClients; i++) {
			Random rand = new Random();
			int processingTime = 0;
			int arrivalTime = 0;
			try {
				processingTime = rand.ints(minProcessingTime, maxProcessingTime).limit(1).findFirst().getAsInt();
			} catch (IllegalArgumentException e) {
				processingTime = minProcessingTime;
			}
			try {
				arrivalTime = rand.ints(minArrivingTime, maxArrivingTime).limit(1).findFirst().getAsInt();
			} catch (IllegalArgumentException e) {
				arrivalTime = minArrivingTime;
			}
			Task task = new Task(arrivalTime, processingTime, i);
			generatedTasks.add(task);
			averageServiceTime += processingTime;
		}
		// sort tasks based on their arrival time
		Collections.sort(generatedTasks, new ArrivalTimeComparator());
		averageServiceTime /= this.numberOfClients;
	}

	/**
	 * Starts the simulation after the initial setup from the UI
	 */
	public void startSimulation() {
		// process the inputs
		try {
			this.numberOfServers = Integer.parseInt(frame.getNumberOfQueues());
			this.minProcessingTime = Integer.parseInt(frame.getMinServiceTime());
			this.maxProcessingTime = Integer.parseInt(frame.getMaxServiceTime());
			this.numberOfClients = Integer.parseInt(frame.getNumberOfClients());
			this.timeLimit = Integer.parseInt(frame.getSimulationInterval());
			this.selectionPolicy = frame.getSelectionPolicy();
			this.minArrivingTime = Integer.parseInt(frame.getMinArrivingTime());
			this.maxArrivingTime = Integer.parseInt(frame.getMaxArrivingTime());
			this.maxTasksPerServer = Integer.parseInt(frame.getTasksPerServer());
			this.simulationSpeed = Integer.parseInt(frame.getSimulationSpeed());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		generateNRandomTasks();
		scheduler = new Scheduler(this.numberOfServers, this.maxTasksPerServer, this);
		scheduler.changeStrategy(this.selectionPolicy);
		scheduler.setMaxTasksPerServer(this.maxTasksPerServer);
		// initialise emptyQueueTime
		emptyQueueTime = new int[this.numberOfServers];
		for (int i = 0; i < this.numberOfServers; i++)
			this.emptyQueueTime[i] = 0;

		Thread th = new Thread(this);
		th.start();
	}

	/**
	 * calculates the step when there were the most people waiting in queues
	 */
	private void calculatePeakHour() {
		int sum = 0;
		// sum up all the tasks that are waiting in queues
		for (int i = 0; i < this.numberOfServers; i++) {
			sum += this.scheduler.getServers().get(i).getTasks().length;
		}

		if (sum > peakHourClients) {
			peakHourClients = sum;
			peakHour = currentTime;
		}
	}

	/**
	 * calculates empty Queue time
	 */
	private void calculateEmptyQueueTime() {
		for (int i = 0; i < this.numberOfServers; i++) {
			if (this.scheduler.getServers().get(i).getTasks().length == 0) {
				this.emptyQueueTime[i]++;
			}
		}
	}

	/**
	 * in every step it adds the chosen tasks to the scheduler which then adds to
	 * the servers based on a strategy
	 */
	private void addTaskToServer() {
		writer.println();
		System.out.println();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (Iterator<Task> iterator = this.generatedTasks.iterator(); iterator.hasNext();) {
			Task t = iterator.next();
			writer.println("current time=" + currentTime + " ,tasks not assigned " + t.getArrivalTime() + " "
					+ t.getprocessingTime());
			System.out.println("current time=" + currentTime + " ,tasks not assigned " + t.getArrivalTime() + " "
					+ t.getprocessingTime());
			if (t.getArrivalTime() == currentTime || t.isLateProcessing()) {
				// if it can be dispatched
				if (this.scheduler.dispatchTask(t) == true)
					iterator.remove();
				else
					t.setLateProcessing(true);
			}
		}
	}

	/**
	 * make a 2d array from all the tasks from the servers
	 */
	private Task[][] getTasks() {
		List<Server> servers = this.scheduler.getServers();
		Task[][] tasks = new Task[servers.size()][];
		for (int i = 0; i < servers.size(); i++) {
			tasks[i] = servers.get(i).getTasks();
		}
		return tasks;
	}

	/**
	 * Contains the actual simulation, in every step cuurentTime will be incremented
	 * with 1 runs until currentTime < timeLimit
	 */
	public void run() {
		currentTime = 0;
		while (currentTime < timeLimit) {
			// add chosen task(s) to the scheduler
			addTaskToServer();
			// calculate the peak hour in every step and empty queue time
			calculatePeakHour();
			calculateEmptyQueueTime();
			// show the evolution of the queues
			frame.displayData(getTasks(), generatedTasks, currentTime);
			currentTime++;
			try {
				Thread.sleep(simulationSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// the time is over, stop other threads from running
		frame.displayData(getTasks(), generatedTasks, currentTime);
		this.scheduler.stopServers();
		// wait one more second before showing statistics
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.dispayStatistics();

		writer.close();
	}

	public static void main(String[] args) {
		SimulatorManager gen = new SimulatorManager();
	}
}
