import lombok.SneakyThrows;

public class On2 implements Runnable {
    Polynomial A;
    Polynomial B;
    Polynomial C;
    Integer nrThreads;

    public On2(Polynomial a, Polynomial b, Integer nrThreads) {
        A = a;
        B = b;
        C = new Polynomial();
        this.nrThreads = nrThreads;
    }

    @SneakyThrows
    @Override
    public void run() {
        Polynomial A = this.A;
        Polynomial B = this.B;

        if (A.terms.size() == 1) {
            this.C = B.oneTermMultiplication(A);
            return;
        } else if (B.terms.size() == 1) {
            this.C = B.oneTermMultiplication(A);
            return;
        }

        Polynomial Au = new Polynomial();
        Polynomial Al = new Polynomial();
        A.halve(Au, Al);

        Polynomial Bu = new Polynomial();
        Polynomial Bl = new Polynomial();
        B.halve(Bu, Bl);

        On2 AuBuOn, AuBlOn, AlBuOn, AlBlOn;

        if (nrThreads <= 1) {
            AuBuOn = new On2(Au, Bu, 1);
            AuBlOn = new On2(Au, Bl, 1);
            AlBuOn = new On2(Al, Bu, 1);
            AlBlOn = new On2(Al, Bl, 1);

            AuBuOn.run();
            AuBlOn.run();
            AlBuOn.run();
            AlBlOn.run();
        } else {
            AuBuOn = new On2(Au, Bu, nrThreads - 3 * nrThreads / 4);
            AuBlOn = new On2(Au, Bl, nrThreads / 4);
            AlBuOn = new On2(Al, Bu, nrThreads / 4);
            AlBlOn = new On2(Al, Bl, nrThreads / 4);

            AuBlOn.run();
            Thread t2 = new Thread(AuBuOn);
            Thread t3 = new Thread(AlBuOn);
            Thread t4 = new Thread(AlBlOn);

            t2.start();
            t3.start();
            t4.start();

            t2.join();
            t3.join();
            t4.join();
        }

        this.C.degree = this.A.degree * this.B.degree;
        this.C.terms = new Terms();
        this.C.terms.addAll(AuBuOn.C.terms);
        this.C.terms.addAll(AuBlOn.C.terms);
        this.C.terms.addAll(AlBuOn.C.terms);
        this.C.terms.addAll(AlBlOn.C.terms);
    }
}