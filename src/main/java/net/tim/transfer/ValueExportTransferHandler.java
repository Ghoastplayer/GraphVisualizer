package net.tim.transfer;

import net.tim.model.Node;

import javax.swing.*;
import java.awt.datatransfer.*;

public class ValueExportTransferHandler extends TransferHandler {
    private final Node node;

    public ValueExportTransferHandler(Node node) {
        this.node = node;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new NodeTransferable(node);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
}

