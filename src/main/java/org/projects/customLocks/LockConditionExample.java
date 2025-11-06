package org.projects.customLocks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This example demonstrates the use of ReentrantLock along with Condition for thread synchronization and inter-thread communication.
 * It simulates a producer-consumer scenario where the producer produces an item and the consumer consumes it.
 * The Condition is used to signal between the producer and consumer threads.
 * Awaiting threads release the lock and reacquire it when signaled. It is similar to using wait().
 * SignalAll() wakes up all waiting threads, similar to notifyAll().
 */
public class LockConditionExample {
    boolean isAvailable = false;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void produce() throws InterruptedException {
        lock.lock();
        System.out.println("Lock acquired by thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
            while (isAvailable) {
                System.out.println("Item already produced, thread " + Thread.currentThread().getName() + " is waiting.");
                condition.await();
            }
            System.out.println("Producing item by thread: " + Thread.currentThread().getName());
            isAvailable = true;
            System.out.println("Signaling consumer thread by thread: " + Thread.currentThread().getName());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();
        System.out.println("Lock acquired by thread: " + Thread.currentThread().getName());
        try {
            while (!isAvailable) {
                System.out.println("No item to consume, thread " + Thread.currentThread().getName() + " is waiting.");
                condition.await();
            }
            System.out.println("Consuming item by thread: " + Thread.currentThread().getName());
            isAvailable = false;
            System.out.println("Signaling producer thread by thread: " + Thread.currentThread().getName());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockConditionExample example = new LockConditionExample();

        Thread producerThread = new Thread(() -> {
            try {
                for(int i = 0; i < 2; i++) {
                    example.produce();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "ProducerThread");

        Thread consumerThread = new Thread(() -> {
            try {
                for(int i = 0; i < 2; i++) {
                    example.consume();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "ConsumerThread");

        consumerThread.start();
        producerThread.start();
    }
}
