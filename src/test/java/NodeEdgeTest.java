import net.tim.model.Node;
import net.tim.model.Edge;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeEdgeTest {
    private Node node1;
    private Node node2;
    private Edge edge;

    @Before
    public void setUp() {
        node1 = new Node(100, 200, "Node1");
        node2 = new Node(300, 400, "Node2");
    }

    @Test
    public void nodeInitialization_SetsCorrectValues() {
        assertEquals(100, node1.x);
        assertEquals(200, node1.y);
        assertEquals("Node1", node1.name);
    }

    @Test
    public void edgeInitialization_SetsCorrectValues() {
        edge = new Edge(node1, node2);
        assertEquals(node1, edge.from);
        assertEquals(node2, edge.to);
        assertFalse(edge.isDirected);
        assertEquals(1, edge.weight);
    }

    @Test
    public void directedEdgeInitialization_SetsCorrectValues() {
        edge = new Edge(node1, node2, true);
        assertEquals(node1, edge.from);
        assertEquals(node2, edge.to);
        assertTrue(edge.isDirected);
        assertEquals(1, edge.weight);
    }

    @Test
    public void weightedEdgeInitialization_SetsCorrectValues() {
        edge = new Edge(node1, node2, false, 5);
        assertEquals(node1, edge.from);
        assertEquals(node2, edge.to);
        assertFalse(edge.isDirected);
        assertEquals(5, edge.weight);
    }

    @Test
    public void weightedDirectedEdgeInitialization_SetsCorrectValues() {
        edge = new Edge(node1, node2, true, 10);
        assertEquals(node1, edge.from);
        assertEquals(node2, edge.to);
        assertTrue(edge.isDirected);
        assertEquals(10, edge.weight);
    }
}