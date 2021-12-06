import java.util.*;

public class Graph {
    Nodes nodes;
    Node startNode;

    Random rand = new Random();

    public void generateHamiltonian(Integer noNodes) {
        this.nodes = new Nodes();
        Map<Integer, Node> cycle = new HashMap<>(noNodes + 1);

        populate(noNodes);

        for (Node node: this.nodes.values()) {
            cycle.put(node.id, node);
        }

        this.startNode = this.nodes.get(0);

        Node currentNode = this.startNode;
        cycle.remove(this.startNode.id);
        int path = 0;
        while (path < noNodes - 1) {
            int node = rand.nextInt(cycle.size());
            Node nextNode = new ArrayList<>(cycle.values()).get(node);
            if(!currentNode.links.containsKey(nextNode.id))
                currentNode.links.put(nextNode.id, nextNode);
            currentNode = nextNode;
            cycle.remove(currentNode.id);
            path++;
        }
        if(!currentNode.links.containsKey(startNode.id))
            currentNode.links.put(startNode.id, startNode);
    }

    public void generateNonHamiltonian(Integer noNodes) {
        this.nodes = new Nodes();

        populate(noNodes);

        this.startNode = this.nodes.get(0);

        for (Node node : this.nodes.values()) {
            if (!Objects.equals(node.id, this.startNode.id))
                node.links.remove(this.startNode.id);
        }
    }

    private void populate(Integer noNodes) {
        for (int i = 0; i < noNodes; i++) {
            Node node = new Node(i);
            node.links = new Nodes();
            this.nodes.put(i, node);
        }

        for (Node node : this.nodes.values()) {
            int noLinks = rand.nextInt(noNodes);
            for (int j = 0; j < noLinks; j++) {
                if (j != node.id)
                    node.links.put(j, this.nodes.get(j));
            }
        }
    }

    public Graph clone() {
        Graph newGraph = new Graph();
        newGraph.nodes = nodes.clone();
        newGraph.startNode = newGraph.nodes.get(0);
        return newGraph;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("startNode: ").append(startNode.id).append("\n");
        for(Node node: this.nodes.values()) {
            sb.append(node.id).append(": [ ");
            for(int key: node.links.keySet()){
                sb.append(" ").append(key).append(",");
            }
            sb.delete(sb.length()-1, sb.length());
            sb.append("  ]\n");
        }
        return sb.toString();
    }
}
