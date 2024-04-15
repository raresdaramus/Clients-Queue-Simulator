package org.example.GUI;

import org.example.BusinessLogic.SimulationManager;
import org.example.BusinessLogic.Strategy.SelectionPolicy;

import javax.swing.*;

public class SimulationForm extends JFrame{
    private JTextField ClientsInput;
    private JTextField QueuesInput;
    private JTextField MinArrival;
    private JTextField MaxArrival;
    private JTextField MinService;
    private JTextField MaxService;
    private JComboBox<String> StrategyBox;  // Ensure the JComboBox is generic to String if you're using strings
    private JButton startButton;
    private JTextArea OutputText;
    private JPanel setupPanel;
    private JPanel MainPanel;
    private JTextField Time;

    public SimulationForm() {
        setContentPane(MainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setTitle("Queue Simulation");

        StrategyBox.addItem("Time Strategy");  // Add time strategy option
        StrategyBox.addItem("Queue Strategy");

        addListeners();
    }

    private void addListeners() {
        startButton.addActionListener(e -> onStart());
    }

    private void onStart() {
        try {
            int time = Integer.parseInt(Time.getText());
            int numberOfClients = Integer.parseInt(ClientsInput.getText());
            int numberOfQueues = Integer.parseInt(QueuesInput.getText());
            int minArrivalTime = Integer.parseInt(MinArrival.getText());
            int maxArrivalTime = Integer.parseInt(MaxArrival.getText());
            int minServiceTime = Integer.parseInt(MinService.getText());
            int maxServiceTime = Integer.parseInt(MaxService.getText());
            String strategy = (String) StrategyBox.getSelectedItem();

            // Now, use these values to configure and start the simulation
            OutputText.append("Starting simulation with the following settings:\n");
            OutputText.append("TimeLimit: " + time + "\n");
            OutputText.append("Number of Clients: " + numberOfClients + "\n");
            OutputText.append("Number of Queues: " + numberOfQueues + "\n");
            OutputText.append("Arrival Times: " + minArrivalTime + " to " + maxArrivalTime + "\n");
            OutputText.append("Service Times: " + minServiceTime + " to " + maxServiceTime + "\n");
            OutputText.append("Strategy: " + strategy + "\n");
            OutputText.append("\n");

            SelectionPolicy policy = strategy.equals("Time Strategy") ? SelectionPolicy.SHORTEST_TIME : SelectionPolicy.SHORTEST_QUEUE;

            SimulationManager simulationManager = new SimulationManager(time, numberOfQueues, numberOfClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, policy, OutputText);
            Thread simThread = new Thread(simulationManager);
            simThread.start();

            // Call to start simulation manager (You need to implement this part according to your application logic)
            // Example: simulationManager.startSimulation(settings);
        } catch (NumberFormatException ex) {
            OutputText.append("Error: Please check your inputs. All inputs must be valid numbers.\n");
        }
    }

    public static void main(String[] args) {

        try {
            // Set Nimbus look and feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimulationForm().setVisible(true);
            }
        });


    }

}
