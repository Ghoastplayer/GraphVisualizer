package net.tim.view;

import net.tim.controller.GraphController;
import net.tim.model.Edge;
import net.tim.model.Node;
import net.tim.transfer.ValueImportTransferHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {

    private GraphController graphController;
    private Node firstSelectedNode, secondSelectedNode, clickedNode, draggedNode;
    private Edge clickedEdge;
    private JPopupMenu nodeMenu, edgeMenu;

    public GraphPanel() {
        initializeMenus();
        addMouseListeners();
    }

    private void initializeMenus() {
        nodeMenu = createNodeMenu();
        edgeMenu = createEdgeMenu();
    }

    private JPopupMenu createNodeMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createMenuItem("Rename", e -> renameNode()));
        menu.add(createMenuItem("Delete", e -> deleteNode()));
        menu.add(createMenuItem("Set Color", e -> setNodeColor()));
        return menu;
    }

    private JPopupMenu createEdgeMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createMenuItem("Change Weight", e -> changeEdgeWeight()));
        menu.add(createMenuItem("Delete", e -> deleteEdge()));
        menu.add(createMenuItem("Set Color", e -> setEdgeColor()));
        return menu;
    }

    private JMenuItem createMenuItem(String title, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(action);
        return item;
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            private int oldX, oldY;

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggedNode = getNodeAt(e.getX(), e.getY());
                    if (draggedNode != null) {
                        oldX = draggedNode.x;
                        oldY = draggedNode.y;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedNode != null) {
                    graphController.moveNode(draggedNode, draggedNode.x, draggedNode.y, oldX, oldY);
                    draggedNode = null;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.x = Math.max(0, Math.min(e.getX(), getWidth()));
                    draggedNode.y = Math.max(0, Math.min(e.getY(), getHeight()));
                    repaint();
                }
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            Node clickedNode = getNodeAt(e.getX(), e.getY());
            if (clickedNode != null) {
                if (clickedNode.equals(firstSelectedNode)) {
                    firstSelectedNode = null;
                } else if (clickedNode.equals(secondSelectedNode)) {
                    secondSelectedNode = null;
                } else if (firstSelectedNode == null) {
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

    private void setNodeColor() {
        if (clickedNode != null) {
            Color newColor = JColorChooser.showDialog(null, "Choose Node Color", clickedNode.color);
            if (newColor != null) {
                graphController.setNodeColor(clickedNode, newColor);
            }
        }
    }

    private void setEdgeColor() {
        if (clickedEdge != null) {
            Color newColor = JColorChooser.showDialog(null, "Choose Edge Color", clickedEdge.color);
            if (newColor != null) {
                graphController.setEdgeColor(clickedEdge, newColor);
            }
        }
    }

    private void renameNode() {
        if (clickedNode != null) {
            String newName = JOptionPane.showInputDialog("Enter new name:");
            if (newName != null && !newName.trim().isEmpty()) {
                graphController.renameNode(clickedNode, newName);
                repaint();
            }
        }
    }

    private void deleteNode() {
        if (clickedNode != null) {
            graphController.removeNode(clickedNode);
            if (clickedNode.equals(firstSelectedNode) || clickedNode.equals(secondSelectedNode)) {
                firstSelectedNode = null;
                secondSelectedNode = null;
            }
            repaint();
        }
    }

    private void changeEdgeWeight() {
        if (clickedEdge != null) {
            String weightStr = JOptionPane.showInputDialog("Enter new weight:");
            if (weightStr != null) {
                try {
                    graphController.setEdgeWeight(clickedEdge, Integer.parseInt(weightStr));
                    repaint();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid weight.");
                }
            }
        }
    }

    private void deleteEdge() {
        if (clickedEdge != null) {
            graphController.removeEdge(clickedEdge);
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
            graphController.addEdge(firstSelectedNode, secondSelectedNode, isDirected, weight);
            firstSelectedNode = null;
            secondSelectedNode = null;
        } else {
            JOptionPane.showMessageDialog(this, "Please select two nodes first.");
        }
    }

    private Node getNodeAt(int x, int y) {
        return graphController.getNodes().stream()
                .filter(node -> Math.abs(node.x - x) < 10 && Math.abs(node.y - y) < 10)
                .findFirst().orElse(null);
    }

    private Edge getEdgeAt(int x, int y) {
        return graphController.getEdges().stream()
                .filter(edge -> pointToLineDistance(x, y, edge.from.x, edge.from.y, edge.to.x, edge.to.y) < 10)
                .findFirst().orElse(null);
    }

    private double pointToLineDistance(int x, int y, int x1, int y1, int x2, int y2) {
        double A = x - x1, B = y - y1, C = x2 - x1, D = y2 - y1;
        double dot = A * C + B * D, len_sq = C * C + D * D, param = len_sq != 0 ? dot / len_sq : -1;
        double xx = param < 0 ? x1 : param > 1 ? x2 : x1 + param * C;
        double yy = param < 0 ? y1 : param > 1 ? y2 : y1 + param * D;
        return Math.sqrt((x - xx) * (x - xx) + (y - yy) * (y - yy));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        graphController.getEdges().forEach(edge -> drawEdge(g, edge));
        graphController.getNodes().forEach(node -> drawNode(g, node));
        highlightSelectedNodes(g);
    }

    private void drawEdge(Graphics g, Edge edge) {
        g.setColor(edge.color);
        g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
        if (edge.isDirected) drawArrow(g, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
        if (edge.weight != 1) {
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(String.valueOf(edge.weight), (edge.from.x + edge.to.x) / 2, (edge.from.y + edge.to.y) / 2);
        }
    }

    private void drawNode(Graphics g, Node node) {
        g.setColor(node.color);
        g.fillOval(node.x - 10, node.y - 10, 20, 20);
        g.setColor(Color.BLACK);
        g.drawString(node.name, node.x - 10, node.y - 15);
    }

    private void highlightSelectedNodes(Graphics g) {
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
        double D = Math.sqrt(dx * dx + dy * dy), arrowLength = 20, arrowWidth = 7, nodeRadius = 9;
        D -= nodeRadius;
        x2 = (int) (x1 + D * (dx / D));
        y2 = (int) (x1 + D * (dy / D));
        double xm = D - arrowLength, xn = xm, ym = arrowWidth, yn = -arrowWidth, x, sin = dy / D, cos = dx / D;
        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;
        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;
        g.fillPolygon(new int[]{x2, (int) xm, (int) xn}, new int[]{y2, (int) ym, (int) yn}, 3);
        g.drawLine(x1, y1, x2, y2);
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
        setTransferHandler(new ValueImportTransferHandler(graphController, this));
    }
}