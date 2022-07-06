import java.util.HashMap;

public class Nodes extends HashMap<Integer, Node> {

    public Nodes clone() {
        Nodes clone = (Nodes) super.clone();
        Nodes nodes = new Nodes();
        for(Node node: this.values()){
            Node newNode = new Node(node.id);
            nodes.put(node.id, newNode);
        }
        for(Node node: nodes.values()){
            Node correspondingNode = this.get(node.id);
            for(Node link: correspondingNode.links.values()){
                node.links.put(link.id, nodes.get(link.id));
            }
        }

        return nodes;
    }
}
