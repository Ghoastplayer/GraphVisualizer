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

    public void addEdge(Node from, Node to, boolean isDirected, int weight) {
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
            switch (action.type()) {
                case ADD_NODE -> graph.removeNode(action.node());
                case REMOVE_NODE -> graph.addNode(action.node().x, action.node().y, action.node().name);
                case ADD_EDGE -> graph.removeEdge(action.edge());
                case REMOVE_EDGE -> graph.addEdge(action.edge().from, action.edge().to);
                case SET_NODE_COLOR -> action.node().color = (Color) action.oldValue();
                case SET_EDGE_COLOR -> action.edge().color = (Color) action.oldValue();
                case SET_EDGE_WEIGHT -> action.edge().weight = (int) action.oldValue();
                case RENAME_NODE -> action.node().name = (String) action.oldValue();
                case MOVE_NODE -> {
                    Point oldPosition = (Point) action.oldValue();
                    action.node().x = oldPosition.x;
                    action.node().y = oldPosition.y;
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

    public void resetColors() {
        graph.getNodes().forEach(node -> node.color = Color.BLACK);
        graph.getEdges().forEach(edge -> edge.color = Color.BLACK);
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
        if (isConnected()) return false;
        return graph.getNodes().stream().allMatch(node -> graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 == 0);
    }

    private boolean isEulerianPath() {
        if (isConnected()) return false;
        return graph.getNodes().stream().filter(node -> graph.getEdges().stream().filter(e -> e.from.equals(node) || e.to.equals(node)).count() % 2 != 0).count() == 2;
    }

    private boolean isConnected() {
        Set<Node> visited = new HashSet<>();
        dfs(graph.getNodes().getFirst(), visited);
        return !graph.getNodes().stream().allMatch(node -> graph.getEdges().stream().noneMatch(e -> e.from.equals(node) || e.to.equals(node)) || visited.contains(node));
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
        Node current = graphCopy.getNodes().getFirst();
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

                Node current = graphCopy.getNodes().stream().filter(node -> nodeEdgesMap.getOrDefault(node, Collections.emptyList()).size() % 2 != 0).findFirst().orElse(graphCopy.getNodes().getFirst());

                while (!nodeEdgesMap.getOrDefault(current, Collections.emptyList()).isEmpty()) {
                    List<Edge> edges = nodeEdgesMap.get(current);
                    Edge edge = edges.getFirst();
                    Node next = edge.from.equals(current) ? edge.to : edge.from;

                    if (!isBridge(graphCopy, edge)) {
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

    // Check if removing edge makes graph disconnected
    private boolean isBridge(Graph graphCopy, Edge edge) {
        graphCopy.removeEdge(edge);
        boolean isBridge = isConnected();
        graphCopy.addEdge(edge.from, edge.to);
        return isBridge;
    }

    private void logAction(String actionType) {
        System.out.println("Action created: " + actionType);
        System.out.println("Stack size: " + actionStack.size());
    }

    private boolean isHamiltonianCircuit() {
        List<Node> path = new ArrayList<>();
        return findHamiltonianCircuit(graph.getNodes().getFirst(), path, new HashSet<>());
    }

    private boolean isHamiltonianPath() {
        for (Node startNode : graph.getNodes()) {
            List<Node> path = new ArrayList<>();
            if (findHamiltonianPath(startNode, path, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean findHamiltonianCircuit(Node current, List<Node> path, Set<Node> visited) {
        path.add(current);
        visited.add(current);

        if (path.size() == graph.getNodes().size()) {
            return graph.getEdges().stream()
                    .anyMatch(e -> e.from.equals(current) && e.to.equals(path.getFirst()));
        }

        for (Edge edge : graph.getEdges()) {
            if (edge.from.equals(current) || edge.to.equals(current)) {
                Node next = edge.from.equals(current) ? edge.to : edge.from;
                if (!visited.contains(next)) {
                    if (findHamiltonianCircuit(next, path, visited)) {
                        return true;
                    }
                }
            }
        }

        path.removeLast();
        visited.remove(current);
        return false;
    }


    private boolean findHamiltonianPath(Node current, List<Node> path, Set<Node> visited) {
        path.add(current);
        visited.add(current);

        if (path.size() == graph.getNodes().size()) {
            return true;
        }

        for (Edge edge : graph.getEdges()) {
            Node next = edge.from.equals(current) ? edge.to : edge.from;
            if (!visited.contains(next)) {
                if (findHamiltonianPath(next, path, visited)) {
                    return true;
                }
            }
        }

        path.removeLast();
        visited.remove(current);
        return false;
    }

    public void markHamiltonian() {
        if (isHamiltonianCircuit()) {
            System.out.println("Hamiltonian circuit found");
            markHamiltonianCircuit();
        } else if (isHamiltonianPath()) {
            System.out.println("Hamiltonian path found");
            markHamiltonianPath();
        }
    }

    private void markHamiltonianCircuit() {
        List<Node> path = new ArrayList<>();
        if (findHamiltonianCircuit(graph.getNodes().getFirst(), path, new HashSet<>())) {
            markPath(path, Color.GREEN);
            Node first = path.getFirst();
            Node last = path.getLast();
            graph.getEdges().stream()
                    .filter(e -> (e.from.equals(first) && e.to.equals(last)) || (e.from.equals(last) && e.to.equals(first)))
                    .findFirst().ifPresent(closingEdge -> closingEdge.color = Color.GREEN);
        }
        graphPanel.repaint();
    }

    private void markHamiltonianPath() {
        for (Node startNode : graph.getNodes()) {
            List<Node> path = new ArrayList<>();
            if (findHamiltonianPath(startNode, path, new HashSet<>())) {
                markPath(path, Color.YELLOW);
                break;
            }
        }
    }

    private void markPath(List<Node> path, Color color) {
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = path.get(i);
            Node to = path.get(i + 1);
            graph.getEdges().stream().filter(e -> (e.from.equals(from) && e.to.equals(to)) || (e.from.equals(to) && e.to.equals(from))).findFirst().ifPresent(edge -> edge.color = color);
        }
        graphPanel.repaint();
    }

    public void colorGraph() {
        Map<Node, Color> nodeColors = new HashMap<>();
        List<Color> colors = Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN);

        for (Node node : graph.getNodes()) {
            Set<Color> usedColors = new HashSet<>();
            for (Edge edge : graph.getEdges()) {
                if (edge.from.equals(node) && nodeColors.containsKey(edge.to)) {
                    usedColors.add(nodeColors.get(edge.to));
                } else if (edge.to.equals(node) && nodeColors.containsKey(edge.from)) {
                    usedColors.add(nodeColors.get(edge.from));
                }
            }
            for (Color color : colors) {
                if (!usedColors.contains(color)) {
                    nodeColors.put(node, color);
                    break;
                }
            }
        }

        markColoredNodes(nodeColors);
    }

    private void markColoredNodes(Map<Node, Color> nodeColors) {
        for (Map.Entry<Node, Color> entry : nodeColors.entrySet()) {
            entry.getKey().setColor(entry.getValue());
        }
        graphPanel.repaint();
    }
}