#include <iostream>
#include <thread>
#include <chrono>
#include <fstream>

#define N 1000
#define threads 20

int getElement(int row, int col, int a[N][N], int b[N][N] ) {
    int sum = 0;
    for(int i = 0; i < N; i ++)
        sum += a[row][i] * b[i][col];
    return sum;
}

void f(int no_elems,
       int start_row,
       int start_col,
       int a[N][N],
       int b[N][N],
       int result[N][N]) {
    int i = 0;
    int row = start_row;
    int col = start_col;

    while(i < no_elems){
        if(col == N){
            row++;
            col=0;
        }
        result[row][col] = getElement(row, col, a, b);
        col++;
        i++;
    }
}

int main() {

    std::thread ts[threads];

    int a[N][N];
    int result[N][N];

    int N2 = N*N;
    int no_elems = N2/threads;
    int rest_elems = (N2)%threads;
    for(auto & i : a)
        std::fill( std::begin(i), std::begin(i) + N, 1);

    int start_row = 0;
    int start_col = 0;

    auto start = std::chrono::high_resolution_clock::now();

    for(int i = 0; i < threads; i++){
        int elems = no_elems;
        if(i == threads-1)
            elems += rest_elems;
        ts[i] = std::thread(f, elems, start_row, start_col, a, a, result);
        start_col = start_col + elems;
        if (start_col >= N) {
            start_row += start_col/N;
            start_col %= N;
        }
    }

    auto stop = std::chrono::high_resolution_clock::now();

    for (auto & t : ts){
        t.join();
    }

    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(stop - start);

    std::ofstream file;
    file.open("noPool.txt", std::ios_base::app);
    file << N << "," << threads << "," << duration.count() << "\n";
    file.close();

    return 0;
}
