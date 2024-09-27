package net.tim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GraphPanel extends JPanel {
    private final Graph graph;
    private Node firstSelectedNode;
    private Node secondSelectedNode;
    private JPopupMenu nodeMenu;
    private JPopupMenu edgeMenu;
    private Node clickedNode;
    private Edge clickedEdge;
    private Node draggedNode;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setTransferHandler(new ValueImportTransferHandler(graph, this));
        initializeNodeMenu();
        initializeEdgeMenu();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Node clickedNode = getNodeAt(e.getX(), e.getY());
                    if (clickedNode != null) {
                        if (firstSelectedNode == null) {
                            firstSelectedNode = clickedNode;
                        } else if (secondSelectedNode == null) {
                            secondSelectedNode = clickedNode;
                        }
                    }
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    clickedNode = getNodeAt(e.getX(), e.getY());
                    clickedEdge = getEdgeAt(e.getX(), e.getY());
                    if (clickedNode != null) {
                        nodeMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (clickedEdge != null) {
                        edgeMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggedNode = getNodeAt(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    int newX = e.getX();
                    int newY = e.getY();
                    // Ensure the node stays within the panel bounds
                    newX = Math.max(0, Math.min(newX, getWidth()));
                    newY = Math.max(0, Math.min(newY, getHeight()));
                    draggedNode.x = newX;
                    draggedNode.y = newY;
                    repaint();
                }
            }
        });
    }

    private void initializeNodeMenu() {
        nodeMenu = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem deleteItem = new JMenuItem("Delete");

        renameItem.addActionListener(e -> renameNode());
        deleteItem.addActionListener(e -> deleteNode());

        nodeMenu.add(renameItem);
        nodeMenu.add(deleteItem);
    }

    private void initializeEdgeMenu() {
        edgeMenu = new JPopupMenu();
        JMenuItem changeWeightItem = new JMenuItem("Change Weight");
        JMenuItem deleteItem = new JMenuItem("Delete");

        changeWeightItem.addActionListener(e -> changeEdgeWeight());
        deleteItem.addActionListener(e -> deleteEdge());

        edgeMenu.add(changeWeightItem);
        edgeMenu.add(deleteItem);
    }

    private void renameNode() {
        if (clickedNode != null) {
            String newName = JOptionPane.showInputDialog("Enter new name:");
            if (newName != null && !newName.trim().isEmpty()) {
                clickedNode.name = newName;
                repaint();
            }
        }
    }

    private void deleteNode() {
        if (clickedNode != null) {
            graph.removeNode(clickedNode);
            repaint();
        }
    }

    private void changeEdgeWeight() {
        if (clickedEdge != null) {
            String weightStr = JOptionPane.showInputDialog("Enter new weight:");
            if (weightStr != null) {
                try {
                    int newWeight = Integer.parseInt(weightStr);
                    clickedEdge.weight = newWeight;
                    repaint();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid weight.");
                }
            }
        }
    }

    private void deleteEdge() {
        if (clickedEdge != null) {
            graph.removeEdge(clickedEdge);
            repaint();
        }
    }

    public void createEdge(boolean isDirected, boolean isWeighted) {
        if (firstSelectedNode != null && secondSelectedNode != null) {
            int weight = 1;
            if (isWeighted) {
                String weightStr = JOptionPane.showInputDialog("Enter edge weight:");
                if (weightStr != null) {
                    try {
                        weight = Integer.parseInt(weightStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid weight. Using default weight 1.");
                    }
                }
            }
            if (isDirected) {
                graph.addWeightedDirectedEdge(firstSelectedNode, secondSelectedNode, weight);
            } else {
                graph.addWeightedEdge(firstSelectedNode, secondSelectedNode, weight);
            }
            firstSelectedNode = null;
            secondSelectedNode = null;
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select two nodes first.");
        }
    }

    private Node getNodeAt(int x, int y) {
        for (Node node : graph.getNodes()) {
            if (Math.abs(node.x - x) < 10 && Math.abs(node.y - y) < 10) {
                return node;
            }
        }
        return null;
    }

    private Edge getEdgeAt(int x, int y) {
        System.out.println("getEdgeAt");
        for (Edge edge : graph.getEdges()) {
            int x1 = edge.from.x;
            int y1 = edge.from.y;
            int x2 = edge.to.x;
            int y2 = edge.to.y;

            // Calculate the distance from the point (x, y) to the line segment (x1, y1) - (x2, y2)
            double distance = pointToLineDistance(x, y, x1, y1, x2, y2);
            if (distance < 10) { // Adjust the threshold as needed
                return edge;
            }
        }
        return null;
    }

    private double pointToLineDistance(int x, int y, int x1, int y1, int x2, int y2) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) { // in case of 0 length line
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Edge edge : graph.getEdges()) {
            g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            if (edge.isDirected) {
                drawArrow(g, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            }
            if (edge.weight != 1) { // Only display weight if it's not the default value
                g.setColor(Color.BLUE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString(String.valueOf(edge.weight), (edge.from.x + edge.to.x) / 2, (edge.from.y + edge.to.y) / 2);
            }
        }
        for (Node node : graph.getNodes()) {
            g.setColor(Color.RED);
            g.fillOval(node.x - 10, node.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(node.name, node.x - 10, node.y - 15);
        }
        if (firstSelectedNode != null) {
            g.setColor(Color.BLUE);
            g.drawOval(firstSelectedNode.x - 15, firstSelectedNode.y - 15, 30, 30);
        }
        if (secondSelectedNode != null) {
            g.setColor(Color.GREEN);
            g.drawOval(secondSelectedNode.x - 15, secondSelectedNode.y - 15, 30, 30);
        }
    }

    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double arrowLength = 20; // Length of the arrowhead
        double arrowWidth = 7;   // Width of the arrowhead
        double xm = D - arrowLength, xn = xm, ym = arrowWidth, yn = -arrowWidth, x;
        double sin = dy / D, cos = dx / D;
        double nodeRadius = 9;

        // Adjust the arrow position to stop at the node's edge (considering node radius)
        D -= nodeRadius; // Subtract node radius to stop the arrow before the node's boundary
        x2 = (int) (x1 + D * cos);
        y2 = (int) (y1 + D * sin);

        // Recalculate points for the arrowhead
        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        // Draw the arrowhead
        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};
        g.fillPolygon(xpoints, ypoints, 3);

        // Draw the main line of the arrow
        g.drawLine(x1, y1, x2, y2);
    }
}

