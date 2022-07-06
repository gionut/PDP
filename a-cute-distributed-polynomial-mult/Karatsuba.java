import lombok.SneakyThrows;

public class Karatsuba implements Runnable {
    Polynomial A;
    Polynomial B;
    Polynomial C;
    Integer nrThreads;

    public Karatsuba(Polynomial a, Polynomial b, Integer nrThreads) {
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

        if (A.terms.size() == 0) {
            this.C = B;
            return;
        } else if (B.terms.size() == 0) {
            this.C = A;
            return;
        }

        if (A.terms.size() == 1) {
            this.C = B.oneTermMultiplication(A);
            return;
        } else if (B.terms.size() == 1) {
            this.C = B.oneTermMultiplication(A);
            return;
        }

        Integer N = Math.max(A.degree, B.degree) + 1;

        Polynomial Au = new Polynomial();
        Polynomial Al = new Polynomial();
        A.KaratsubaHalve(Au, Al, N);

        Polynomial Bu = new Polynomial();
        Polynomial Bl = new Polynomial();
        B.KaratsubaHalve(Bu, Bl, N);

        Karatsuba D0, D1, D01;

        if (nrThreads <= 1) {
            D0 = new Karatsuba(Al, Bl, 1);
            D1 = new Karatsuba(Au, Bu, 1);
            D01 = new Karatsuba(Al.Sum(Au, 1), Bl.Sum(Bu, 1), 1);

            D0.run();
            D1.run();
            D01.run();

        } else {
            D0 = new Karatsuba(Al, Bl, nrThreads - 2 * nrThreads / 3);
            D1 = new Karatsuba(Au, Bu, nrThreads / 3);
            D01 = new Karatsuba(Al.Sum(Au, 1), Bl.Sum(Bu, 1), nrThreads / 3);

            D0.run();
            Thread t2 = new Thread(D1);
            Thread t3 = new Thread(D01);

            t2.start();
            t3.start();

            t2.join();
            t3.join();
        }

        Polynomial K1 = D1.C.RankUp(N);
        Polynomial K2 = D01.C.Sum(D0.C, -1).Sum(D1.C, -1).RankUp(N / 2);

        this.C = K1.Sum(K2.Sum(D0.C, 1), 1);
    }
}
