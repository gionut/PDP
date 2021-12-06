import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

enum State {SUCCESS}

public class HamiltonianCycle implements Runnable{
    NodesStack path;
    Integer nrThreads;
    Graph graph;
    Node startNode;
    State state;

    public HamiltonianCycle(NodesStack path, Integer nrThreads, Graph graph, Node startNode) {
        this.path = path;
        this.nrThreads = nrThreads;
        this.graph = graph;
        this.startNode = startNode;
    }

    @SneakyThrows
    public void run() {
        path.add(0, startNode);

        if (startNode.id == graph.startNode.id) {
            if (path.size() - 1 == graph.nodes.size()) {
                this.state = State.SUCCESS;
                return;
            }
            if (path.size() > 1)
                return;
        }
        int threadsLeft = nrThreads;
        List<Thread> threads = new ArrayList<>();
        List<Visit> visits = new ArrayList<>();
        for (Node node : this.startNode.links.values()) {
            if (!node.isVisited) {
                if (nrThreads <= 1) {
                    Visit v = new Visit();
                    v.h = this;
                    v.nrThreads = nrThreads;
                    v.node = node;
                    visits.add(v);
                    v.run();
                } else {
                    Visit v = new Visit();
                    v.h = this;
                    v.nrThreads = nrThreads/node.links.size();
                    v.node = node;
                    visits.add(v);
                    Thread t = new Thread(v);
                    threads.add(t);
                    t.start();
                    threadsLeft = threadsLeft - nrThreads / node.links.size();
                }
            }
        }
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
            Visit v = visits.get(i);
            if (v.h.state == State.SUCCESS) {
                this.state = State.SUCCESS;
                this.path = v.h.path;
            }
        }
    }
}

class Visit implements Runnable{
    HamiltonianCycle h;
    Node node;
    int nrThreads;

    @Override
    public void run() {
        node.isVisited = true;
        HamiltonianCycle H;
        H = new HamiltonianCycle(h.path.clone(), nrThreads, h.graph.clone(), node);
        H.run();

        if(H.state == State.SUCCESS) {
            h.path = H.path;
            h.state = State.SUCCESS;
            return;
        }
        h.path.remove(node);
        node.isVisited = false;
    }
}