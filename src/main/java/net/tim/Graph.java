package net.tim;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addWeightedEdge(Node from, Node to, int weight) {
        edges.add(new Edge(from, to, false, weight));
    }

    public void addWeightedDirectedEdge(Node from, Node to, int weight) {
        edges.add(new Edge(from, to, true, weight));
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

    public void clear() {
        nodes.clear();
        edges.clear();
    }

    public void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Node node : nodes) {
                writer.write("NODE " + node.x + " " + node.y + " " + node.name);
                writer.newLine();
            }
            for (Edge edge : edges) {
                writer.write("EDGE " + edge.from.name + " " + edge.to.name + " " + edge.weight + " " + edge.isDirected);
                writer.newLine();
            }
        }
    }

    public void loadFromFile(File file) throws IOException {
        clear();
        Map<String, Node> nodeMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("NODE")) {
                    Node node = new Node(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3]);
                    nodes.add(node);
                    nodeMap.put(node.name, node);
                } else if (parts[0].equals("EDGE")) {
                    Node from = nodeMap.get(parts[1]);
                    Node to = nodeMap.get(parts[2]);
                    int weight = Integer.parseInt(parts[3]);
                    boolean isDirected = Boolean.parseBoolean(parts[4]);
                    edges.add(new Edge(from, to, isDirected, weight));
                }
            }
        }
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