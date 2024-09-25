package net.tim;

import java.util.ArrayList;
import java.util.List;

class Graph {
    private final List<Node> nodes;
    private final List<Edge> edges;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(int x, int y, String name) {
        nodes.add(new Node(x, y, name));
    }

    public void addEdge(Node from, Node to) {
        edges.add(new Edge(from, to));
    }

    public void addDirectedEdge(Node from, Node to) {
        edges.add(new Edge(from, to, true));
    }

    public void removeNode(Node node) {
        nodes.remove(node);
        edges.removeIf(edge -> edge.from == node || edge.to == node);
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}

class Node {
    int x, y;
    String name;

    public Node(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
}

class Edge {
    Node from, to;
    int weight;
    boolean isDirected;

    public Edge(Node from, Node to) {
        this(from, to, false);
    }

    public Edge(Node from, Node to, boolean isDirected) {
        this.from = from;
        this.to = to;
        this.isDirected = isDirected;
    }
}