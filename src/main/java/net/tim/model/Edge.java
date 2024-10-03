package net.tim.model;

import java.awt.Color;

public class Edge {
    public Node from, to;
    public boolean isDirected;
    public int weight;
    public Color color;

    public Edge(Node from, Node to) {
        this(from, to, false, 1);
    }

    public Edge(Node from, Node to, boolean isDirected) {
        this(from, to, isDirected, 1);
    }

    public Edge(Node from, Node to, boolean isDirected, int weight) {
        this.from = from;
        this.to = to;
        this.isDirected = isDirected;
        this.weight = weight;
        this.color = Color.BLACK;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}