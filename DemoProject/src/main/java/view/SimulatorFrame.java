package view;

import control.SimulatorManager;
import model.SelectionPolicy;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulatorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private SimulatorManager model;

	private Container content;

	// first panel components
	private JPanel panel;
	private JTextField t_queues = new JTextField("3", 25);
	private JTextField t_minProc = new JTextField("2", 25);
	private JTextField t_maxProc = new JTextField("5", 25);
	private JTextField t__numberClients = new JTextField("12", 25);
	private JTextField t_interval_min = new JTextField("1", 25);
	private JTextField t_interval_max = new JTextField("9", 25);
	private JTextField t_tasksPerServer = new JTextField("10", 25);
	private JTextField t_interval = new JTextField("15", 25);
	private JTextField t_speed = new JTextField("1000", 25);

	private JLabel l_empty2 = new JLabel("");
	private JLabel l_empty = new JLabel("");
	private JLabel l_queues = new JLabel("Number of queues");
	private JLabel l_minProc = new JLabel("min Service Time");
	private JLabel l__maxProc = new JLabel("max Service Time");
	private JLabel l__numberClients = new JLabel("number of Clients");
	private JLabel l__selectionPolicy = new JLabel("Selection Policy ");
	private JLabel l__interval_min = new JLabel("Minimum arriving time between customers");
	private JLabel l__interval_max = new JLabel("Maximum arriving time between customers");
	private JLabel l_tasksPerServer = new JLabel("Maximum Tasks per server");
	private JLabel l__interval = new JLabel("Simulation interval");
	private JLabel l_speed = new JLabel("Simulation speed");

	private JList<String> s_selectionPolicy;

	// second panel components
	private JPanel panel2;
	private boolean firstTime = true;
	private JLabel l_currentTime = new JLabel("Current Time=0", JLabel.CENTER);
	private JLabel l_title = new JLabel("ENTER SIMULATION PARAMETERS", JLabel.CENTER);

	// third panel components
	private JPanel panel3;
	private JLabel l_generatedTasks = new JLabel("", JLabel.CENTER);;
	private JButton b_startBtn = new JButton("START");

	public SimulatorFrame(SimulatorManager model) {
		content = getContentPane();
		content.setLayout((new BorderLayout()));
		this.model = model;
		GridLayout layout = new GridLayout(12, 2);
		panel = new JPanel(layout);
		panel2 = new JPanel(new BorderLayout());
		panel3 = new JPanel(new BorderLayout());

		DefaultListModel<String> policies = new DefaultListModel<String>();
		policies.addElement("QUEUE");
		policies.addElement("TIME");
		s_selectionPolicy = new JList<String>(policies);
		s_selectionPolicy.setSelectedIndex(0);

		panel.add(l_empty2);
		panel.add(l_empty);
		panel.add(l_queues);
		panel.add(t_queues);
		panel.add(l_minProc);
		panel.add(t_minProc);
		panel.add(l__maxProc);
		panel.add(t_maxProc);
		panel.add(l__numberClients);
		panel.add(t__numberClients);
		panel.add(l__selectionPolicy);
		panel.add(s_selectionPolicy);
		panel.add(l__interval_min);
		panel.add(t_interval_min);
		panel.add(l__interval_max);
		panel.add(t_interval_max);
		panel.add(l_tasksPerServer);
		panel.add(t_tasksPerServer);
		panel.add(l__interval);
		panel.add(t_interval);
		panel.add(l_speed);
		panel.add(t_speed);

		addListeners();

		l_title.setForeground(Color.orange);
		l_title.setFont(new Font("Verdana", Font.BOLD, 24));
		panel2.add(l_title, BorderLayout.NORTH);

		b_startBtn.setFont(new Font("Verdana", Font.BOLD, 24));
		panel3.add(b_startBtn, BorderLayout.NORTH);

		content.add(panel, BorderLayout.CENTER);
		content.add(panel2, BorderLayout.NORTH);
		content.add(panel3, BorderLayout.SOUTH);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setName("QUEUE BASED SIMULATION");
		this.setVisible(true);
	}

	/**
	 * Adds listeners to the buttons
	 */
	public void addListeners() {
		b_startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				content.removeAll();
				content.repaint();
				add(panel);
				model.startSimulation();
			}
		});
	}

	/**
	 * This method is called in every step during the Simulation and it shows the
	 * evolution of the queues in every step
	 */
	public void displayData(Task[][] tasks, List<Task> generatedTasks, int currentTime) {
		// remove all previous elements
		panel.removeAll();
		content.removeAll();
		content.repaint();

		content.setLayout((new BorderLayout()));
		GridLayout layout = new GridLayout(2, model.getNumberOfServers());
		panel.setLayout(layout);
		panel2 = new JPanel(new BorderLayout());
		panel3 = new JPanel(new BorderLayout());

		// panel2
		if (currentTime < model.getTimeLimit())
			l_currentTime.setText("Current Time=" + currentTime);
		else
			l_currentTime.setText("SIMULATION END");

		l_currentTime.setForeground(Color.orange);
		l_currentTime.setFont(new Font("Verdana", Font.BOLD, 26));
		panel2.add(l_currentTime, BorderLayout.NORTH);

		// panel3
		l_generatedTasks.setText("Remaining Tasks");
		l_generatedTasks.setForeground(Color.orange);
		l_generatedTasks.setFont(new Font("Verdana", Font.BOLD, 24));
		panel3.add(l_generatedTasks, BorderLayout.NORTH);

		DefaultListModel listModel;
		listModel = new DefaultListModel();
		for (int i = 0; i < generatedTasks.size(); i++) {
			listModel.addElement(generatedTasks.get(i));
		}
		// Create the list and put it in a scroll pane.
		JList list;
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);
		panel3.add(listScrollPane, BorderLayout.SOUTH);

		for (int i = 0; i < model.getNumberOfServers(); i++) {
			JLabel l_serverNumber = new JLabel("<html>Queue number " + i + "<br/> waiting period="
					+ model.getScheduler().getServers().get(i).getWaitingPeriod() + "<html>");
			l_serverNumber.setForeground(Color.orange);
			l_serverNumber.setFont(new Font("Verdana", Font.BOLD, 18));
			l_serverNumber.setVerticalAlignment(JLabel.BOTTOM);
			l_serverNumber.setHorizontalAlignment(JLabel.CENTER);
			panel.add(l_serverNumber);
		}
		for (int i = 0; i < model.getNumberOfServers(); i++) {
			// System.out.println("i="+i);
			listModel = new DefaultListModel();
			for (int j = 0; j < tasks[i].length; j++) {
				// System.out.println("j="+tasks[i][j]+" ");
				listModel.addElement(tasks[i][j]);
			}
			// Create the list and put it in a scroll pane.
			list = new JList(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setVisibleRowCount(5);
			listScrollPane = new JScrollPane(list);
			panel.add(listScrollPane);
		}

		content.add(panel, BorderLayout.CENTER);
		content.add(panel2, BorderLayout.NORTH);
		content.add(panel3, BorderLayout.SOUTH);
		// pack it for the first time only, because its size won't change during the
		// simulation
		if (firstTime == true) {
			this.pack();
			firstTime = false;
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Displays the statistics at the final stage
	 */
	public void dispayStatistics() {
		panel.removeAll();
		panel2.removeAll();
		content.removeAll();
		content.repaint();

		content.setLayout((new BorderLayout()));

		GridLayout layout = new GridLayout(3 + model.getNumberOfServers(), 1);
		panel.setLayout(layout);
		panel2 = new JPanel(new BorderLayout());

		JLabel l_statistics = new JLabel("SIMULATION STATISTICS");
		l_statistics.setForeground(Color.orange);
		l_statistics.setFont(new Font("Verdana", Font.BOLD, 28));
		panel2.add(l_statistics, BorderLayout.NORTH);

		JLabel l_stat1 = new JLabel(
				"Peak hour: " + model.getPeakHour() + " with " + model.getPeakHourClients() + " tasks");
		JLabel l_stat2 = new JLabel("Average waiting time: " + model.getAverageWaitingTime());
		JLabel l_stat3 = new JLabel("Average Service Time: " + model.getAverageServiceTime());

		panel.add(l_stat1);
		panel.add(l_stat2);
		panel.add(l_stat3);

		for (int i = 0; i < model.getNumberOfServers(); i++) {
			JLabel l_emptyTime = new JLabel(
					"Empty time for queue " + i + " is " + model.getEmptyQueueTime()[i] + " steps");
			panel.add(l_emptyTime);
		}

		content.add(panel, BorderLayout.CENTER);
		content.add(panel2, BorderLayout.NORTH);

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void addStartListener(ActionListener mal) {
		b_startBtn.addActionListener(mal);
	}

	public String getMaxServiceTime() {
		return t_maxProc.getText();
	}

	public String getMinServiceTime() {
		return t_minProc.getText();
	}

	public String getNumberOfClients() {
		return t__numberClients.getText();
	}

	public SelectionPolicy getSelectionPolicy() {
		if (s_selectionPolicy.getSelectedIndex() == 0)
			return SelectionPolicy.SHORTEST_QUEUE;
		else
			return SelectionPolicy.SHORTEST_TIME;
	}

	public String getNumberOfQueues() {
		return t_queues.getText();
	}

	public String getMinArrivingTime() {
		return t_interval_min.getText();
	}

	public String getMaxArrivingTime() {
		return t_interval_max.getText();
	}

	public String getSimulationInterval() {
		return t_interval.getText();
	}

	public String getTasksPerServer() {
		return t_tasksPerServer.getText();
	}

	public String getSimulationSpeed() {
		return t_speed.getText();
	}

}
