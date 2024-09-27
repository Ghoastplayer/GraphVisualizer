package net.tim.transfer;

import net.tim.controller.GraphController;
import net.tim.model.Graph;
import net.tim.model.Node;

import javax.swing.*;
import java.awt.*;

public class ValueImportTransferHandler extends TransferHandler {
    private final GraphController graphController;
    private final JPanel panel;

    public ValueImportTransferHandler(GraphController graphController, JPanel panel) {
        this.graphController = graphController;
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
                graphController.addNode(dropPoint.x, dropPoint.y, nodeName);
                panel.repaint();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}