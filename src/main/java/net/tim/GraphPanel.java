package net.tim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GraphPanel extends JPanel {
    private Graph graph;
    private Node firstSelectedNode;
    private Node secondSelectedNode;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setTransferHandler(new ValueImportTransferHandler(graph, this));
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
                    } else {
                        String nodeName = JOptionPane.showInputDialog("Enter node name:");
                        if (nodeName != null) {
                            graph.addNode(e.getX(), e.getY(), nodeName);
                            System.out.println("Node added at (" + e.getX() + ", " + e.getY() + ")");
                        }
                    }
                    repaint();
                }
            }
        });
    }

    public void createEdge(boolean isDirected) {
        if (firstSelectedNode != null && secondSelectedNode != null) {
            if (isDirected) {
                graph.addDirectedEdge(firstSelectedNode, secondSelectedNode);
            } else {
                graph.addEdge(firstSelectedNode, secondSelectedNode);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Edge edge : graph.getEdges()) {
            g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            if (edge.isDirected) {
                drawArrow(g, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
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

class ValueImportTransferHandler extends TransferHandler {
    private final Graph graph;
    private final JPanel panel;

    public ValueImportTransferHandler(Graph graph, JPanel panel) {
        this.graph = graph;
        this.panel = panel;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(NodeTransferable.NODE_FLAVOR);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        try {
            Node node = (Node) support.getTransferable().getTransferData(NodeTransferable.NODE_FLAVOR);
            Point dropPoint = support.getDropLocation().getDropPoint();
            String nodeName = JOptionPane.showInputDialog("Enter node name:");
            if (nodeName != null) {
                graph.addNode(dropPoint.x, dropPoint.y, nodeName);
                panel.repaint();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}