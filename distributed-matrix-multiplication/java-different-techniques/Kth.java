public class Kth implements Runnable{
    int N;
    int no_elems;
    int start_row;
    int start_col;
    int step;
    int[][] a;
    int[][] b;
    int[][] result;

    public Kth(int N, int no_elems, int start_row, int start_col, int step, int[][] a, int[][] b, int[][] result) {
        this.N = N;
        this.no_elems = no_elems;
        this.start_row = start_row;
        this.start_col = start_col;
        this.step = step;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    int getElement(int row, int col) {
        int sum = 0;
        for(int i = 0; i < N; i ++)
            sum += a[row][i] * b[i][col];
        return sum;
    }

    @Override
    public void run() {
        int i = 0;
        int row = start_row;
        int col = start_col;

        while(i < no_elems){
            if(col >= N){
                row++;
                col = col%N;
            }
            result[row][col] = getElement(row, col);
            col+=step;
            i++;
        }
    }
}
