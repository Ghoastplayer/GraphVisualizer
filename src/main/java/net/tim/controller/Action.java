package net.tim.controller;

import net.tim.model.Edge;
import net.tim.model.Node;

public class Action {
    public enum ActionType { ADD_NODE, REMOVE_NODE, ADD_EDGE, REMOVE_EDGE, SET_NODE_COLOR, SET_EDGE_COLOR, SET_EDGE_WEIGHT, RENAME_NODE, MOVE_NODE }

    private final ActionType type;
    private final Node node;
    private final Edge edge;
    private final Object oldValue;
    private final Object newValue;

    public Action(ActionType type, Node node, Edge edge, Object oldValue, Object newValue) {
        this.type = type;
        this.node = node;
        this.edge = edge;
        this.oldValue = oldValue;
        this.newValue = newValue;
        System.out.println("Action created: " + type + " " + node + " " + edge + " " + oldValue + " " + newValue);
    }

    public ActionType getType() {
        return type;
    }

    public Node getNode() {
        return node;
    }

    public Edge getEdge() {
        return edge;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}