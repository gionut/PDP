import java.util.ArrayList;
import java.util.Objects;

public class NodesStack extends ArrayList<Node> {

    public NodesStack clone() {
        NodesStack clone = (NodesStack) super.clone();
        NodesStack nodes = new NodesStack();
        for(Node node: this){
            Node newNode = new Node(node.id);
            nodes.add(newNode);
        }
        for(int i = 0; i < this.size(); i++){
            Node newNode = nodes.get(i);
            Node correspondingNode = this.get(i);
            for(Node link: correspondingNode.links.values()){
                newNode.links.put(link.id, link);
            }
        }
        return nodes;
    }
}
