#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <time.h>

#define BUFFER_SIZE 10
#define V_SIZE 1000000

typedef struct {
    int storage[BUFFER_SIZE];
    int size;
    int next_free;
    int first_occupied;
    pthread_mutex_t mutex;
    pthread_cond_t free_spots;
    pthread_cond_t consume;

} buffer_ds;

buffer_ds buffer;

void* producer_f(buffer_ds* buff) {
    printf("producer\n");

    int v1[V_SIZE], v2[V_SIZE];
    time_t t;
    srand((unsigned) time(&t));

    int i;
    int true_sum = 0;
    for(i = 0; i < V_SIZE; i++) {
        v1[i] = 1;//rand()%10 + 1;
        v2[i] = 1;//rand()%10 + 1;
        true_sum += v1[i] * v2[i];
    }
    printf("true sum is %d\n", true_sum);

    for(i = 0; i < V_SIZE; i++) {
        int product = v1[i] * v2[i];

        pthread_mutex_lock(&buff->mutex);
        while(buff->size >= BUFFER_SIZE)
            pthread_cond_wait(&buff->free_spots, &buff->mutex);

        buff->storage[buff->next_free++] = product;
        buff->next_free %= BUFFER_SIZE;

        buff->size++;

        pthread_cond_signal(&buff->consume);
        pthread_mutex_unlock(&buff->mutex);
    }

    return NULL;
}

void* consumer_f(buffer_ds* buff) {
    printf("consumer\n");

    int sum = 0;

    int i = 0;
    while(i < V_SIZE) {

        pthread_mutex_lock(&buff->mutex);
        while(buff->size <= 0)
            pthread_cond_wait(&buff->consume, &buff->mutex);

        int product = buff->storage[buff->first_occupied++];
        buff->first_occupied %= BUFFER_SIZE;
        buff->size--;

        pthread_cond_signal(&buff->free_spots);
        pthread_mutex_unlock(&buff->mutex);

        sum = sum + product;
        i++;

    }

    printf("computed sum is %d\n", sum);
    return NULL;
}


int main() {
    pthread_t producer, consumer;
    buffer.size = 0;
    buffer.first_occupied = 0;
    buffer.next_free = 0;

    pthread_create(&producer, NULL, (void*)producer_f, &buffer);
    pthread_create(&consumer, NULL, (void*)consumer_f, &buffer);

    pthread_join(producer, NULL);
    pthread_join(consumer, NULL);

    return 0;
}
