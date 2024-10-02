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
        System.out.println("Action created: ADD_NODE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void setNodeColor(Node node, Color color) {
        Color oldColor = node.color;
        node.color = color;
        actionStack.push(new Action(Action.ActionType.SET_NODE_COLOR, node, null, oldColor, color));
        System.out.println("Action created: SET_NODE_COLOR");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void setEdgeColor(Edge edge, Color color) {
        Color oldColor = edge.color;
        edge.color = color;
        actionStack.push(new Action(Action.ActionType.SET_EDGE_COLOR, null, edge, oldColor, color));
        System.out.println("Action created: SET_EDGE_COLOR");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void setEdgeWeight(Edge edge, int weight) {
        int oldWeight = edge.weight;
        edge.weight = weight;
        actionStack.push(new Action(Action.ActionType.SET_EDGE_WEIGHT, null, edge, oldWeight, weight));
        System.out.println("Action created: SET_EDGE_WEIGHT");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void renameNode(Node node, String newName) {
        String oldName = node.name;
        node.name = newName;
        actionStack.push(new Action(Action.ActionType.RENAME_NODE, node, null, oldName, newName));
        System.out.println("Action created: RENAME_NODE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void addEdge(Node from, Node to, boolean isDirected, boolean isWeighted, int weight) {
        Edge edge = new Edge(from, to, isDirected, weight);
        graph.addEdge(edge.from, edge.to);
        actionStack.push(new Action(Action.ActionType.ADD_EDGE, null, edge, null, null));
        System.out.println("Action created: ADD_EDGE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void removeNode(Node node) {
        graph.removeNode(node);
        actionStack.push(new Action(Action.ActionType.REMOVE_NODE, node, null, null, null));
        System.out.println("Action created: REMOVE_NODE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void removeEdge(Edge edge) {
        graph.removeEdge(edge);
        actionStack.push(new Action(Action.ActionType.REMOVE_EDGE, null, edge, null, null));
        System.out.println("Action created: REMOVE_EDGE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void moveNode(Node node, int newX, int newY, int oldX, int oldY) {
        node.x = newX;
        node.y = newY;
        actionStack.push(new Action(Action.ActionType.MOVE_NODE, node, null, new Point(oldX, oldY), new Point(newX, newY)));
        System.out.println("Action created: MOVE_NODE");
        System.out.println("Stack size: " + actionStack.size());
        graphPanel.repaint();
    }

    public void undo() {
        System.out.println("Undoing action...");
        System.out.println("Stack size before undo: " + actionStack.size());
        if (!actionStack.isEmpty()) {
            Action action = actionStack.pop();
            System.out.println("Undoing action: " + action.getType());
            switch (action.getType()) {
                case ADD_NODE:
                    System.out.println("Removing node: " + action.getNode().name);
                    graph.removeNode(action.getNode());
                    break;
                case REMOVE_NODE:
                    System.out.println("Adding node: " + action.getNode().name);
                    graph.addNode(action.getNode().x, action.getNode().y, action.getNode().name);
                    break;
                case ADD_EDGE:
                    System.out.println("Removing edge from: " + action.getEdge().from.name + " to: " + action.getEdge().to.name);
                    graph.removeEdge(action.getEdge());
                    break;
                case REMOVE_EDGE:
                    System.out.println("Adding edge from: " + action.getEdge().from.name + " to: " + action.getEdge().to.name);
                    graph.addEdge(action.getEdge().from, action.getEdge().to);
                    break;
                case SET_NODE_COLOR:
                    System.out.println("Reverting node color: " + action.getNode().name);
                    action.getNode().color = (Color) action.getOldValue();
                    break;
                case SET_EDGE_COLOR:
                    System.out.println("Reverting edge color from: " + action.getEdge().from.name + " to: " + action.getEdge().to.name);
                    action.getEdge().color = (Color) action.getOldValue();
                    break;
                case SET_EDGE_WEIGHT:
                    System.out.println("Reverting edge weight from: " + action.getEdge().from.name + " to: " + action.getEdge().to.name);
                    action.getEdge().weight = (int) action.getOldValue();
                    break;
                case RENAME_NODE:
                    System.out.println("Reverting node name: " + action.getNode().name);
                    action.getNode().name = (String) action.getOldValue();
                    break;
                case MOVE_NODE:
                    Point oldPosition = (Point) action.getOldValue();
                    action.getNode().x = oldPosition.x;
                    action.getNode().y = oldPosition.y;
                    break;
            }
            graphPanel.repaint();
        } else {
            System.out.println("No actions to undo.");
        }
        System.out.println("Stack size after undo: " + actionStack.size());
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
            System.out.println("Graph has an Eulerian Circuit.");
            markEulerianCircuit();
        } else if (isEulerianPath()) {
            System.out.println("Graph has an Eulerian Path.");
            markEulerianPath();
        } else {
            System.out.println("Graph has neither an Eulerian Circuit nor an Eulerian Path.");
        }
    }

    private boolean isEulerianCircuit() {
        if (!isConnected()) return false;
        for (Node node : graph.getNodes()) {
            if (graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isEulerianPath() {
        if (!isConnected()) return false;
        int oddCount = 0;
        for (Node node : graph.getNodes()) {
            if (graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 != 0) {
                oddCount++;
            }
        }
        return oddCount == 2;
    }

    private boolean isConnected() {
        Set<Node> visited = new HashSet<>();
        Node startNode = graph.getNodes().get(0);
        dfs(startNode, visited);
        for (Node node : graph.getNodes()) {
            if (graph.getEdges().stream().anyMatch(e -> e.from.equals(node) || e.to.equals(node)) && !visited.contains(node)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConnected(Graph graphCopy) {
        Set<Node> visited = new HashSet<>();
        Node startNode = graphCopy.getNodes().get(0);
        dfs(graphCopy, startNode, visited);
        for (Node node : graphCopy.getNodes()) {
            if (graphCopy.getEdges().stream().anyMatch(e -> e.from.equals(node) || e.to.equals(node)) && !visited.contains(node)) {
                return false;
            }
        }
        return true;
    }

    private void dfs(Graph graphCopy, Node node, Set<Node> visited) {
        visited.add(node);
        for (Edge edge : graphCopy.getEdges()) {
            if (edge.from.equals(node) && !visited.contains(edge.to)) {
                dfs(graphCopy, edge.to, visited);
            } else if (edge.to.equals(node) && !visited.contains(edge.from)) {
                dfs(graphCopy, edge.from, visited);
            }
        }
    }

    private void dfs(Node node, Set<Node> visited) {
        visited.add(node);
        for (Edge edge : graph.getEdges()) {
            if (edge.from.equals(node) && !visited.contains(edge.to)) {
                dfs(edge.to, visited);
            } else if (edge.to.equals(node) && !visited.contains(edge.from)) {
                dfs(edge.from, visited);
            }
        }
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

        // Mark the circuit in the original graph
        for (Edge edge : circuit) {
            Edge originalEdge = findOriginalEdge(edge);
            if (originalEdge != null) {
                originalEdge.color = Color.RED; // Marking the edge as part of the Eulerian Circuit
            }
        }
        graphPanel.repaint();
    }

    private void markEulerianPath() {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            Graph graphCopy = copyGraph(graph);
            List<Edge> path = new ArrayList<>();
            Map<Node, List<Edge>> nodeEdgesMap = new HashMap<>();

            // Initialize the map with edges for each node
            for (Edge edge : graphCopy.getEdges()) {
                nodeEdgesMap.computeIfAbsent(edge.from, k -> new ArrayList<>()).add(edge);
                nodeEdgesMap.computeIfAbsent(edge.to, k -> new ArrayList<>()).add(edge);
            }

            Node current = graphCopy.getNodes().stream()
                .filter(node -> nodeEdgesMap.getOrDefault(node, Collections.emptyList()).size() % 2 != 0)
                .findFirst()
                .orElse(graphCopy.getNodes().get(0));

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

            // Mark the path in the original graph
            for (Edge edge : path) {
                Edge originalEdge = findOriginalEdge(edge);
                if (originalEdge != null) {
                    originalEdge.color = Color.BLUE; // Marking the edge as part of the Eulerian Path
                }
            }
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
        for (Node node : original.getNodes()) {
            copy.addNode(new Node(node.x, node.y, node.name));
        }
        for (Edge edge : original.getEdges()) {
            Node from = copy.getNodes().stream().filter(n -> n.name.equals(edge.from.name)).findFirst().get();
            Node to = copy.getNodes().stream().filter(n -> n.name.equals(edge.to.name)).findFirst().get();
            copy.addEdge(new Edge(from, to, edge.isDirected, edge.weight));
        }
        return copy;
    }

    private Edge findOriginalEdge(Edge edge) {
        return graph.getEdges().stream().filter(e -> e.from.name.equals(edge.from.name) && e.to.name.equals(edge.to.name) && e.isDirected == edge.isDirected && e.weight == edge.weight).findFirst().orElse(null);
    }

    private boolean isBridge(Graph graphCopy, Node node, Edge edge) {
        graphCopy.removeEdge(edge);
        boolean isBridge = !isConnected(graphCopy);
        graphCopy.addEdge(edge.from, edge.to);
        return isBridge;
    }
}