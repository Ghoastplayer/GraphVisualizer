package net.tim;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

class ValueExportTransferHandler extends TransferHandler {
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

class NodeTransferable implements Transferable {
    private final Node node;
    public static final DataFlavor NODE_FLAVOR = new DataFlavor(Node.class, "A Node Object");

    public NodeTransferable(Node node) {
        this.node = node;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{NODE_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return NODE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return node;
    }
}