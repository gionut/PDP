
public class Main {

    public static void main(String[] args) {
        Polynomial A = new Polynomial();
        A.generate(1000);

        Polynomial B = new Polynomial();
        B.generate(2300);

//        On2 algorithm = new On2(A, B, 16);
        Karatsuba algorithm = new Karatsuba(A, B, 16);
        long startTime = System.currentTimeMillis();
        algorithm.run();
        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);

//        System.out.println(algorithm.A);
//        System.out.println(algorithm.B);
//        System.out.println(algorithm.C);
        System.out.println(duration);
    }

}
