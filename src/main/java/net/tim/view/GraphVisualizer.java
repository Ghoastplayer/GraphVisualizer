package net.tim.view;

import net.tim.controller.GraphController;
import net.tim.model.Graph;
import net.tim.model.Node;
import net.tim.transfer.ValueExportTransferHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GraphVisualizer extends JFrame {
    private final Graph graph;
    private final GraphPanel graphPanel;
    private final GraphController graphController;
    private boolean isDirected = false; // Default to undirected
    private boolean isWeighted = false; // Default to unweighted

    public GraphVisualizer() {
        graph = new Graph();
        graphPanel = new GraphPanel();
        graphController = new GraphController(graph, graphPanel);
        graphPanel.setGraphController(graphController);

        setTitle("Graphen-Visualisierer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem resetMenuItem = new JMenuItem("Reset");

        JMenu algorithmsMenu = new JMenu("Algorithms");
        JMenuItem markEulerianMenuItem = new JMenuItem("Mark Eulerian");
        JMenuItem markHamiltonianMenuItem = new JMenuItem("Mark Hamiltonian");
        JMenuItem colorGraphMenuItem = new JMenuItem("Color Graph");


        saveMenuItem.addActionListener(e -> saveGraph());
        loadMenuItem.addActionListener(e -> loadGraph());
        resetMenuItem.addActionListener(e -> graphController.resetGraph());
        markEulerianMenuItem.addActionListener(e -> graphController.markEulerian());
        markHamiltonianMenuItem.addActionListener(e -> graphController.markHamiltonian());
        colorGraphMenuItem.addActionListener(e -> graphController.colorGraph());

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(resetMenuItem);
        algorithmsMenu.add(markEulerianMenuItem);
        algorithmsMenu.add(markHamiltonianMenuItem);
        algorithmsMenu.add(colorGraphMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(algorithmsMenu);

        setJMenuBar(menuBar);

        //Toolbar on the top
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);
        JButton addEdgeButton = new JButton("Kante Hinzufügen");
        JToggleButton toggleDirectedButton = new JToggleButton("Gerichtet");
        JCheckBox weightedCheckBox = new JCheckBox("Gewichtet");
        JButton undoButton = new JButton("Rückgängig");

        addEdgeButton.addActionListener(e -> graphPanel.createEdge(isDirected, isWeighted));
        undoButton.addActionListener(e -> graphController.undo());

        toggleDirectedButton.addItemListener(e -> {
            isDirected = toggleDirectedButton.isSelected();
            toggleDirectedButton.setText(isDirected ? "Gerichtet" : "Ungerichtet");
        });

        weightedCheckBox.addItemListener(e -> isWeighted = weightedCheckBox.isSelected());

        toggleDirectedButton.setSelected(isDirected);
        toggleDirectedButton.setText(isDirected ? "Gerichtet" : "Ungerichtet");

        controlPanel.add(addEdgeButton);
        controlPanel.add(toggleDirectedButton);
        controlPanel.add(weightedCheckBox);
        controlPanel.add(undoButton);

        add(controlPanel, BorderLayout.NORTH);

        // Toolbar on the right
        JPanel toolbar = new JPanel();
        toolbar.setBackground(Color.LIGHT_GRAY);
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

    private void saveGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Graph files", "graph"));
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                graphController.saveGraph(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Graph files", "graph"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                graphController.loadGraph(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphVisualizer().setVisible(true));
    }
}