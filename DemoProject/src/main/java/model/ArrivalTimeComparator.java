package model;

import java.util.Comparator;

/**
 *
 */
public class ArrivalTimeComparator implements Comparator<Task> {

	public int compare(Task o1, Task o2) {
		return (o1.getArrivalTime() - o2.getArrivalTime());
	}
}
