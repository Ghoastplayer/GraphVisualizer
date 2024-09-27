package net.tim.model;

public class Edge {
    public Node from;
    public Node to;
    public int weight;
    public boolean isDirected;

    public Edge(Node from, Node to) {
        this(from, to, false, 1); // Default weight is 1
    }

    public Edge(Node from, Node to, boolean isDirected) {
        this(from, to, isDirected, 1); // Default weight is 1
    }

    public Edge(Node from, Node to, boolean isDirected, int weight) {
        this.from = from;
        this.to = to;
        this.isDirected = isDirected;
        this.weight = weight;
    }
}