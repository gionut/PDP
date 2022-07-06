import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        int N = 100;
        int N2 = N*N;
        int threads = 4;

        Thread[] ts = new Thread[threads];
//        ExecutorService pool = Executors.newFixedThreadPool(threads);

        int[][] a = new int[N][N];
        int[][] result = new int[N][N];

        Arrays.stream(a).forEach(row -> Arrays.fill(row, 1));

        int no_elems = N2/threads;
        int rest_elems = (N2)%threads;

        int start_row = 0;
        int start_col = 0;

        long startTime = System.currentTimeMillis();

        for(int i = 0; i < threads; i++){
            int elems = no_elems;
            if(i == threads-1)
                elems += rest_elems;

            ts[i] = new Thread(new MyThread(N, elems, start_row, start_col, a, a, result));
            ts[i].start();
//            MyThread thread = new MyThread(N, elems, start_row, start_col, a, a, result);
//            pool.execute(thread);

            start_col = start_col + elems;
            if (start_col >= N) {
                start_row += start_col/N;
                start_col %= N;
            }
        }

//        for(int i = 0; i < threads; i++){
//            int elems = no_elems;
//            if(i == threads-1)
//                elems += rest_elems;
//
//            ts[i] = new Thread(new ColByCol(N, elems, start_row, start_col, a, a, result));
//            ts[i].start();
////            MyThread thread = new MyThread(N, elems, start_row, start_col, a, a, result);
////            pool.execute(thread);
//
//            start_row = start_row + elems;
//            if (start_row >= N) {
//                start_col += start_row/N;
//                start_row %= N;
//            }
//        }

//        int step = N2/no_elems;
//        for(int i = 0; i < threads; i++){
//            int elems = no_elems;
//            if(i == 0)
//                elems += N2%no_elems;
//
////            ts[i] = new Thread(new Kth(N, elems, start_row, start_col, step, a, a, result));
////            ts[i].start();
//            Kth thread = new Kth(N, elems, start_row, start_col, step, a, a, result);
//            pool.execute(thread);
//
//            start_col++;
//        }

        long endTime = System.currentTimeMillis();

//        for (Thread t : ts){
//            t.join();
//        }

//        pool.shutdown();

        long duration = (endTime - startTime);

        for(int i=0; i < N; i++)
            for(int j = 0; j < N; j++) {
//                System.out.println(result[i][j]);
                if (result[i][j] > N) {
                    System.out.println(i + " " + j);
//                    return;
                }
            }

        BufferedWriter file = new BufferedWriter(new FileWriter("src/noPool.txt", true));
        file.write(N + "," + threads + "," + duration + "\n");
        file.close();
    }
}

