package net.tim.controller;

import net.tim.model.Edge;
import net.tim.model.Graph;
import net.tim.model.Node;
import net.tim.view.GraphPanel;

import java.io.File;
import java.io.IOException;

public class GraphController {
    private final Graph graph;
    private final GraphPanel graphPanel;

    public GraphController(Graph graph, GraphPanel graphPanel) {
        this.graph = graph;
        this.graphPanel = graphPanel;
    }

    public void addNode(int x, int y, String name) {
        graph.addNode(x, y, name);
        graphPanel.repaint();
    }

    public void addEdge(Node from, Node to, boolean isDirected, boolean isWeighted, int weight) {
        if (isDirected) {
            if (isWeighted) {
                graph.addWeightedDirectedEdge(from, to, weight);
            } else {
                graph.addDirectedEdge(from, to);
            }
        } else {
            if (isWeighted) {
                graph.addWeightedEdge(from, to, weight);
            } else {
                graph.addEdge(from, to);
            }
        }
        graphPanel.repaint();
    }

    public void removeNode(Node node) {
        graph.removeNode(node);
        graphPanel.repaint();
    }

    public void removeEdge(Edge edge) {
        graph.removeEdge(edge);
        graphPanel.repaint();
    }

    public void saveGraph(File file) throws IOException {
        graph.saveToFile(file);
    }

    public void loadGraph(File file) throws IOException {
        graph.loadFromFile(file);
        graphPanel.repaint();
    }

    public void resetGraph() {
        graph.clear();
        graphPanel.repaint();
    }
}