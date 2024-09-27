package net.tim;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private boolean isDirected = false; // Default to undirected
    private boolean isWeighted = false; // Default to unweighted

    public GraphVisualizer() {
        graph = new Graph();
        graphPanel = new GraphPanel(graph);

        setTitle("Graphen-Visualisierer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Toolbar for actions (e.g., adding nodes/edges)
        JPanel controlPanel = new JPanel();
        JButton addEdgeButton = new JButton("Kante Hinzufügen");
        JToggleButton toggleDirectedButton = new JToggleButton("Gerichtet");
        JCheckBox weightedCheckBox = new JCheckBox("Gewichtet");

        addEdgeButton.addActionListener(e -> graphPanel.createEdge(isDirected, isWeighted));

        toggleDirectedButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isDirected = toggleDirectedButton.isSelected();
                toggleDirectedButton.setText(isDirected ? "Gerichtet" : "Ungerichtet");
            }
        });

        weightedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isWeighted = weightedCheckBox.isSelected();
            }
        });

        // Ensure the initial state is set correctly
        toggleDirectedButton.setSelected(isDirected);
        toggleDirectedButton.setText(isDirected ? "Gerichtet" : "Ungerichtet");

        controlPanel.add(addEdgeButton);
        controlPanel.add(toggleDirectedButton);
        controlPanel.add(weightedCheckBox);

        // Add Save, Load, and Reset buttons
        JButton saveButton = new JButton("Save Graph");
        JButton loadButton = new JButton("Load Graph");
        JButton resetButton = new JButton("Reset Graph");

        saveButton.addActionListener(e -> saveGraph());
        loadButton.addActionListener(e -> loadGraph());
        resetButton.addActionListener(e -> resetGraph());

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.NORTH);

        // Toolbar on the right for dragging nodes
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        JButton nodeButton = new JButton("Node");
        nodeButton.setTransferHandler(new ValueExportTransferHandler(new Node(0, 0, "Unnamed")));
        nodeButton.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, TransferHandler.COPY);
            }
        });
        toolbar.add(nodeButton);
        add(toolbar, BorderLayout.EAST);

        add(graphPanel, BorderLayout.CENTER);
    }

    private void resetGraph() {
        graph.clear();
        graphPanel.repaint();
    }

    private void saveGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Graph files", "graph"));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".graph")) {
                file = new File(file.getAbsolutePath() + ".graph");
            }
            try {
                graph.saveToFile(file);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving graph: " + ex.getMessage());
            }
        }
    }

    private void loadGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Graph files", "graph"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                graph.loadFromFile(file);
                graphPanel.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading graph: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphVisualizer().setVisible(true));
    }
}