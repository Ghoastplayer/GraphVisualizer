package net.tim.controller;

import net.tim.model.Edge;
import net.tim.model.Graph;
import net.tim.model.Node;
import net.tim.view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GraphController {
    private final Graph graph;
    private final GraphPanel graphPanel;
    private final Stack<Action> actionStack = new Stack<>();

    public GraphController(Graph graph, GraphPanel graphPanel) {
        this.graph = graph;
        this.graphPanel = graphPanel;
    }

    public void addNode(int x, int y, String name) {
        Node node = new Node(x, y, name);
        graph.addNode(node.x, node.y, node.name);
        actionStack.push(new Action(Action.ActionType.ADD_NODE, node, null, null, null));
        logAction("ADD_NODE");
        graphPanel.repaint();
    }

    public void setNodeColor(Node node, Color color) {
        Color oldColor = node.color;
        node.color = color;
        actionStack.push(new Action(Action.ActionType.SET_NODE_COLOR, node, null, oldColor, color));
        logAction("SET_NODE_COLOR");
        graphPanel.repaint();
    }

    public void setEdgeColor(Edge edge, Color color) {
        Color oldColor = edge.color;
        edge.color = color;
        actionStack.push(new Action(Action.ActionType.SET_EDGE_COLOR, null, edge, oldColor, color));
        logAction("SET_EDGE_COLOR");
        graphPanel.repaint();
    }

    public void setEdgeWeight(Edge edge, int weight) {
        int oldWeight = edge.weight;
        edge.weight = weight;
        actionStack.push(new Action(Action.ActionType.SET_EDGE_WEIGHT, null, edge, oldWeight, weight));
        logAction("SET_EDGE_WEIGHT");
        graphPanel.repaint();
    }

    public void renameNode(Node node, String newName) {
        String oldName = node.name;
        node.name = newName;
        actionStack.push(new Action(Action.ActionType.RENAME_NODE, node, null, oldName, newName));
        logAction("RENAME_NODE");
        graphPanel.repaint();
    }

    public void addEdge(Node from, Node to, boolean isDirected, boolean isWeighted, int weight) {
        Edge edge = new Edge(from, to, isDirected, weight);
        graph.addEdge(edge.from, edge.to);
        actionStack.push(new Action(Action.ActionType.ADD_EDGE, null, edge, null, null));
        logAction("ADD_EDGE");
        graphPanel.repaint();
    }

    public void removeNode(Node node) {
        graph.removeNode(node);
        actionStack.push(new Action(Action.ActionType.REMOVE_NODE, node, null, null, null));
        logAction("REMOVE_NODE");
        graphPanel.repaint();
    }

    public void removeEdge(Edge edge) {
        graph.removeEdge(edge);
        actionStack.push(new Action(Action.ActionType.REMOVE_EDGE, null, edge, null, null));
        logAction("REMOVE_EDGE");
        graphPanel.repaint();
    }

    public void moveNode(Node node, int newX, int newY, int oldX, int oldY) {
        node.x = newX;
        node.y = newY;
        actionStack.push(new Action(Action.ActionType.MOVE_NODE, node, null, new Point(oldX, oldY), new Point(newX, newY)));
        logAction("MOVE_NODE");
        graphPanel.repaint();
    }

    public void undo() {
        if (!actionStack.isEmpty()) {
            Action action = actionStack.pop();
            switch (action.getType()) {
                case ADD_NODE -> graph.removeNode(action.getNode());
                case REMOVE_NODE -> graph.addNode(action.getNode().x, action.getNode().y, action.getNode().name);
                case ADD_EDGE -> graph.removeEdge(action.getEdge());
                case REMOVE_EDGE -> graph.addEdge(action.getEdge().from, action.getEdge().to);
                case SET_NODE_COLOR -> action.getNode().color = (Color) action.getOldValue();
                case SET_EDGE_COLOR -> action.getEdge().color = (Color) action.getOldValue();
                case SET_EDGE_WEIGHT -> action.getEdge().weight = (int) action.getOldValue();
                case RENAME_NODE -> action.getNode().name = (String) action.getOldValue();
                case MOVE_NODE -> {
                    Point oldPosition = (Point) action.getOldValue();
                    action.getNode().x = oldPosition.x;
                    action.getNode().y = oldPosition.y;
                }
            }
            graphPanel.repaint();
        }
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

    public List<Node> getNodes() {
        return graph.getNodes();
    }

    public List<Edge> getEdges() {
        return graph.getEdges();
    }

    public void markEulerian() {
        if (isEulerianCircuit()) {
            markEulerianCircuit();
        } else if (isEulerianPath()) {
            markEulerianPath();
        }
    }

    private boolean isEulerianCircuit() {
        if (!isConnected()) return false;
        return graph.getNodes().stream().allMatch(node -> graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 == 0);
    }

    private boolean isEulerianPath() {
        if (!isConnected()) return false;
        return graph.getNodes().stream().filter(node -> graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 != 0).count() == 2;
    }

    private boolean isConnected() {
        Set<Node> visited = new HashSet<>();
        dfs(graph.getNodes().get(0), visited);
        return graph.getNodes().stream().allMatch(node -> graph.getEdges().stream().anyMatch(e -> e.from.equals(node) || e.to.equals(node)) ? visited.contains(node) : true);
    }

    private void dfs(Node node, Set<Node> visited) {
        visited.add(node);
        graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).forEach(e -> {
            Node next = e.from.equals(node) ? e.to : e.from;
            if (!visited.contains(next)) dfs(next, visited);
        });
    }

    private void markEulerianCircuit() {
        Graph graphCopy = copyGraph(graph);
        Stack<Node> stack = new Stack<>();
        List<Edge> circuit = new ArrayList<>();
        Node current = graphCopy.getNodes().get(0);
        stack.push(current);

        while (!stack.isEmpty()) {
            Node finalCurrent = current;
            if (graphCopy.getEdges().stream().anyMatch(e -> e.from.equals(finalCurrent) || e.to.equals(finalCurrent))) {
                stack.push(current);
                Node finalCurrent1 = current;
                Edge edge = graphCopy.getEdges().stream().filter(e -> e.from.equals(finalCurrent1) || e.to.equals(finalCurrent1)).findFirst().get();
                circuit.add(edge);
                graphCopy.removeEdge(edge);
                current = edge.from.equals(current) ? edge.to : edge.from;
            } else {
                current = stack.pop();
            }
        }

        circuit.forEach(edge -> {
            Edge originalEdge = findOriginalEdge(edge);
            if (originalEdge != null) originalEdge.color = Color.RED;
        });
        graphPanel.repaint();
    }

    private void markEulerianPath() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                Graph graphCopy = copyGraph(graph);
                List<Edge> path = new ArrayList<>();
                Map<Node, List<Edge>> nodeEdgesMap = new HashMap<>();

                graphCopy.getEdges().forEach(edge -> {
                    nodeEdgesMap.computeIfAbsent(edge.from, k -> new ArrayList<>()).add(edge);
                    nodeEdgesMap.computeIfAbsent(edge.to, k -> new ArrayList<>()).add(edge);
                });

                Node current = graphCopy.getNodes().stream().filter(node -> nodeEdgesMap.getOrDefault(node, Collections.emptyList()).size() % 2 != 0).findFirst().orElse(graphCopy.getNodes().get(0));

                while (!nodeEdgesMap.getOrDefault(current, Collections.emptyList()).isEmpty()) {
                    List<Edge> edges = nodeEdgesMap.get(current);
                    Edge edge = edges.get(0);
                    Node next = edge.from.equals(current) ? edge.to : edge.from;

                    if (!isBridge(graphCopy, current, edge)) {
                        path.add(edge);
                        edges.remove(edge);
                        nodeEdgesMap.get(next).remove(edge);
                        graphCopy.removeEdge(edge);
                        current = next;
                    }
                }

                path.forEach(edge -> {
                    Edge originalEdge = findOriginalEdge(edge);
                    if (originalEdge != null) originalEdge.color = Color.BLUE;
                });
                return null;
            }

            @Override
            protected void done() {
                graphPanel.repaint();
            }
        };
        worker.execute();
    }

    private Graph copyGraph(Graph original) {
        Graph copy = new Graph();
        original.getNodes().forEach(node -> copy.addNode(new Node(node.x, node.y, node.name)));
        original.getEdges().forEach(edge -> {
            Node from = copy.getNodes().stream().filter(n -> n.name.equals(edge.from.name)).findFirst().get();
            Node to = copy.getNodes().stream().filter(n -> n.name.equals(edge.to.name)).findFirst().get();
            copy.addEdge(new Edge(from, to, edge.isDirected, edge.weight));
        });
        return copy;
    }

    private Edge findOriginalEdge(Edge edge) {
        return graph.getEdges().stream().filter(e -> e.from.name.equals(edge.from.name) && e.to.name.equals(edge.to.name) && e.isDirected == edge.isDirected && e.weight == edge.weight).findFirst().orElse(null);
    }

    private boolean isBridge(Graph graphCopy, Node node, Edge edge) {
        graphCopy.removeEdge(edge);
        boolean isBridge = !isConnected();
        graphCopy.addEdge(edge.from, edge.to);
        return isBridge;
    }

    private void logAction(String actionType) {
        System.out.println("Action created: " + actionType);
        System.out.println("Stack size: " + actionStack.size());
    }
}