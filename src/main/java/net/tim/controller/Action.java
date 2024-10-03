package net.tim.controller;

import net.tim.model.Edge;
import net.tim.model.Node;

public record Action(net.tim.controller.Action.ActionType type, Node node, Edge edge, Object oldValue,
                     Object newValue) {

    public enum ActionType {ADD_NODE, REMOVE_NODE, ADD_EDGE, REMOVE_EDGE, SET_NODE_COLOR, SET_EDGE_COLOR, SET_EDGE_WEIGHT, RENAME_NODE, MOVE_NODE}

    public Action(ActionType type, Node node, Edge edge, Object oldValue, Object newValue) {
        this.type = type;
        this.node = node;
        this.edge = edge;
        this.oldValue = oldValue;
        this.newValue = newValue;
        System.out.println("Action created: " + type + " " + node + " " + edge + " " + oldValue + " " + newValue);
    }
}