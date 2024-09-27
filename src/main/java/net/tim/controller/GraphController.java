package net.tim.controller;

import net.tim.model.Edge;
import net.tim.model.Graph;
import net.tim.model.Node;
import net.tim.view.GraphPanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

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
}