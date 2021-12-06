
public class Node {
    Integer id;
    Boolean isVisited;
    Nodes links;

    public Node(Integer id) {
        this.id = id;
        this.isVisited = false;
        links = new Nodes();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
