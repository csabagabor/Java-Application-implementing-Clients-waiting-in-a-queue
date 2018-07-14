package model;

public class Task {
	private int id = 0;
	private int arrivalTime;
	private int processingTime;
	private boolean lateProcessing = false;// if there is no space in server, it needs to be added later

	public Task(int arrivalTime, int processingTime, int id) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.processingTime = processingTime;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getprocessingTime() {
		return processingTime;
	}

	public void setprocessingTime(int processingTime) {
		this.processingTime = processingTime;
	}

	public String toString() {
		return "id=" + id + " ArrTime " + arrivalTime + " PrTime: " + processingTime;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}

	public boolean isLateProcessing() {
		return lateProcessing;
	}

	public void setLateProcessing(boolean lateProcessing) {
		this.lateProcessing = lateProcessing;
	}
}