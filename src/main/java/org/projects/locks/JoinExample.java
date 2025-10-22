package org.projects.locks;

/**
 * This class demonstrates the use of the join() method in Java threading.
 * The main thread starts a producer thread that executes a synchronized method.
 * The main thread waits for the producer thread to finish using join().
 * By calling join(), the main thread ensures that it does not proceed until the producer thread has completed its execution.
 * Main thread is blocked until the producer thread completes.
 */
public class JoinExample {

    public synchronized void produce() throws InterruptedException {
        System.out.println("Lock acquired by thread: " + Thread.currentThread().getName());
        Thread.sleep(2000);
        System.out.println("Lock released by thread: " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        System.out.println("Main thread started");

        JoinExample joinExample = new JoinExample();
        Thread producerThread = new Thread(() -> {
            try {
                System.out.println("Producer thread started and called produce method");
                joinExample.produce();
                System.out.println("Producer thread finished execution");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "ProducerThread");

        producerThread.start();

        try {
            System.out.println("Main thread waiting for producer thread to finish");
            producerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main thread finished execution");
    }
}
