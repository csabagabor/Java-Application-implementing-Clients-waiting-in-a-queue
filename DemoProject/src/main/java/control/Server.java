package control;

import model.Task;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

;

public class Server implements Runnable {

	private int id = 0;
	private BlockingQueue<Task> tasks;
	private AtomicInteger waitingPeriod;
	private boolean running;
	private Scheduler scheduler;

	public Server(int maxTasksPerServer, int id, Scheduler scheduler) {
		running = true;
		waitingPeriod = new AtomicInteger(0);
		tasks = new ArrayBlockingQueue<Task>(maxTasksPerServer);
		this.id = id;
		this.scheduler = scheduler;
	}

	public void addTask(Task newTask) {
		this.tasks.add(newTask);
		// calculate waiting time
		this.scheduler.incrementWaitingTime(waitingPeriod.intValue());
		waitingPeriod.addAndGet(newTask.getprocessingTime());
	}

	public int getWaitingPeriod() {
		return waitingPeriod.intValue();
	}

	public void stop() {
		this.running = false;
	}

	public void run() {
		while (true && running == true) {
			if (this.tasks.iterator().hasNext()) {
				Task nextTask = this.tasks.iterator().next();
				System.out.println("current time=" + this.scheduler.getCurrentTime() + " TASK TAKEN by server with id: "
						+ id + " task:" + nextTask.toString());
				SimulatorManager.writer.println("current time=" + this.scheduler.getCurrentTime()
						+ " TASK TAKEN by server with id: " + id + " task:" + nextTask.toString());
				try {
					Thread.sleep(nextTask.getprocessingTime() * SimulatorManager.simulationSpeed);
					// take out the task only if during the sleep the server was not stopped
					if (this.running == true) {
						System.out.println("current time=" + this.scheduler.getCurrentTime()
								+ " TASK REMOVED by server with id: " + id + " task:" + nextTask.toString());
						SimulatorManager.writer.println("current time=" + this.scheduler.getCurrentTime()
								+ " TASK REMOVED by server with id: " + id + " task:" + nextTask.toString());
						this.waitingPeriod.addAndGet((-1) * nextTask.getprocessingTime());
						this.tasks.take();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public Task[] getTasks() {
		Task[] taskToList = new Task[tasks.size()];
		tasks.toArray(taskToList);
		return taskToList;
	}

	@Override
	public String toString() {
		return "Server{" + "id=" + id + ", tasks=" + tasks + ", waitingPeriod=" + waitingPeriod + '}';
	}
}