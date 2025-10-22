package org.projects.locks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

/**
 * A thread-safe buffer queue implementation with producer-consumer functionality.
 * This class uses synchronized methods along with wait() and notifyAll() to manage
 * access to the buffer, ensuring that producers wait when the buffer is full and
 * consumers wait when the buffer is empty.
 */
public class BufferQueue {
    int size;
    Queue<String> queue;

    public BufferQueue(int size) {
        this.size = size;
        queue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void put(String data) {
        System.out.println("Thread " + Thread.currentThread().getName() + " is trying to put data: " + data);

        while(queue.size() == size) {
            try {
                System.out.println("Buffer is full. Thread " + Thread.currentThread().getName() + " is waiting to put data.");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        queue.add(data);
        System.out.println("Thread " + Thread.currentThread().getName() + " put data: " + data);
        if (queue.size() == 1) {
            System.out.println("Buffer was empty. Notifying all waiting threads.");
            notifyAll();
        }
    }

    public synchronized String poll() {
        System.out.println("Thread " + Thread.currentThread().getName() + " is trying to consume data.");

        while(queue.isEmpty()) {
            try {
                System.out.println("Buffer is empty. Thread " + Thread.currentThread().getName() + " is waiting to consume data.");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String data = queue.poll();
        System.out.println("Thread " + Thread.currentThread().getName() + " consumed data: " + data);

        if (queue.size() == size - 1) {
            System.out.println("Buffer was full. Notifying all waiting threads.");
            notifyAll();
        }
        return data;
    }

    public static void main(String args[]) {

        /* Creating two consumer threads to demonstrate multiple consumers waiting on the buffer */
        /*
        BufferQueue bufferQueue = new BufferQueue(1);

        Thread producer = new Thread(() -> {
            for(int i = 1; i<=2; i++) {
                try {
                    sleep(2000);
                    bufferQueue.put("Item" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "ProducerThread");

        Runnable consumerRunnable = () -> {
            String data = bufferQueue.poll();
            System.out.println("Data consumed: " + data + " by thread: " + Thread.currentThread().getName());
        };

        Thread consumer1 = new Thread(consumerRunnable, "ConsumerThread-1");
        Thread consumer2 = new Thread(consumerRunnable, "ConsumerThread-2");

        consumer1.start();
        consumer2.start();
        producer.start();
        */

        /* Creating one producer and one consumer thread to demonstrate buffer with size greater than one */
        BufferQueue bufferQueue = new BufferQueue(3);

        Thread producer = new Thread(() -> {
            for(int i = 1; i<=6; i++) {
                bufferQueue.put("Item" + i);
            }
        }, "ProducerThread");

        Thread consumer = new Thread(() -> {
            for(int i = 1; i<=6; i++) {
                String data = bufferQueue.poll();
                System.out.println("Data consumed: " + data + " by thread: " + Thread.currentThread().getName());
            }
        }, "ConsumerThread");

        consumer.start();
        producer.start();
    }
}
