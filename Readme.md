# Graph Visualization Tool

## Overview

This project is a Graph Visualization Tool that allows users to create, edit, and visualize graphs. The tool provides a graphical interface for adding nodes and edges, setting their properties, and saving/loading graphs from files.

## Features

- **Add Nodes**: Users can add nodes to the graph by dragging the node from the right toolbar.
- **Add Edges**: Users can add edges between nodes by left-clicking two nodes and then hitting the "Add Edge" button.
- **Edit Nodes**: Users can rename nodes, delete nodes, and set their colors.
- **Edit Edges**: Users can change the weight of edges, delete edges, and set their colors.
- **Drag and Drop**: Nodes can be dragged and repositioned within the graph panel.
- **Save/Load Graphs**: Graphs can be saved to and loaded from files, preserving all node and edge properties.
- **Graph Visualization**: The tool provides a visual representation of the graph, with different colors and styles for nodes and edges.
- **Algorithms**:
  - **Euler Circle**: Perform an Euler circle under `Algorithms > Euler Circle`.
  - **Hamilton Circle**: Perform a Hamilton circle under `Algorithms > Hamilton Circle`.

## Usage

1. **Adding Nodes**: Drag the node from the right toolbar to the desired position on the graph panel.
2. **Adding Edges**: Left-click two nodes and then hit the "Add Edge" button.
3. **Editing Nodes**: Right-click on a node to rename, delete, or set its color.
4. **Editing Edges**: Right-click on an edge to change its weight, delete it, or set its color.
5. **Dragging Nodes**: Click and hold a node to drag it to a new position.
6. **Saving Graphs**: Use the "Save" option to save the current graph to a file.
7. **Loading Graphs**: Use the "Load" option to load a graph from a file.
8. **Performing Algorithms**:
   - **Euler Circle**: Navigate to `Algorithms > Euler Circle` to perform an Euler circle.
   - **Hamilton Circle**: Navigate to `Algorithms > Hamilton Circle` to perform a Hamilton circle.

## Installation

To build and run the project, you need to have Java and Maven installed. Follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/graph-visualization-tool.git
    ```
2. Navigate to the project directory:
    ```sh
    cd graph-visualization-tool
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```
4. Run the application:
    ```sh
    mvn exec:java -Dexec.mainClass="net.tim.Main"
    ```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author

Tim von der Weppen