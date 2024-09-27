package net.tim.model;

import java.awt.Color;

public class Node {
    public int x, y;
    public String name;
    public Color color;

    public Node(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = Color.BLACK; // Default color
    }
}