
public class Main {

    public static void main(String[] args) {
        Graph hamiltonian = new Graph();

        hamiltonian.generateHamiltonian(20);
        Graph nonHamiltonian = new Graph();
//        nonHamiltonian.generateNonHamiltonian(5);

        System.out.println(hamiltonian);
//        System.out.println(nonHamiltonian);

        HamiltonianCycle h = new HamiltonianCycle(new NodesStack(), 1, hamiltonian, hamiltonian.startNode);

        long startTime = System.currentTimeMillis();
        h.run();
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println(duration);
        System.out.println(h.state);
        System.out.println(h.path);


    }

}
