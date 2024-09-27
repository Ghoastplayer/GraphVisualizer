import net.tim.model.Graph;
import net.tim.model.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GraphTest {
    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph();
    }

    @Test
    public void testAddNode() {
        graph.addNode(100, 200, "Node1");
        assertEquals(1, graph.getNodes().size());
        assertEquals("Node1", graph.getNodes().get(0).name);
    }

    @Test
    public void testAddEdge() {
        Node node1 = new Node(100, 200, "Node1");
        Node node2 = new Node(300, 400, "Node2");
        graph.addNode(node1.x, node1.y, node1.name);
        graph.addNode(node2.x, node2.y, node2.name);
        graph.addEdge(node1, node2);
        assertEquals(1, graph.getEdges().size());
        assertEquals(node1, graph.getEdges().get(0).from);
        assertEquals(node2, graph.getEdges().get(0).to);
    }

    @Test
    public void testSaveAndLoadGraph() throws IOException {
        graph.addNode(100, 200, "Node1");
        graph.addNode(300, 400, "Node2");
        Node node1 = graph.getNodes().get(0);
        Node node2 = graph.getNodes().get(1);
        graph.addEdge(node1, node2);

        File file = new File("test.graph");
        graph.saveToFile(file);

        Graph loadedGraph = new Graph();
        loadedGraph.loadFromFile(file);
        assertEquals(2, loadedGraph.getNodes().size());
        assertEquals(1, loadedGraph.getEdges().size());
        assertEquals("Node1", loadedGraph.getNodes().get(0).name);
        assertEquals("Node2", loadedGraph.getNodes().get(1).name);

        // Clean up
        file.delete();
    }

    @Test
    public void testNodeRemoval() {
        Node node1 = new Node(100, 200, "Node1");
        Node node2 = new Node(300, 400, "Node2");
        graph.addNode(node1);
        graph.addNode(node2);
        graph.removeNode(node1);
        assertEquals(1, graph.getNodes().size());
    }
}