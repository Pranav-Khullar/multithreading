package org.projects.customLocks;

import java.util.concurrent.locks.StampedLock;

/**
 * This example demonstrates the use of StampedLock for thread synchronization.
 * The read lock is shared, allowing multiple threads to read simultaneously.
 * Multiple threads can hold read locks simultaneously as long as no thread holds the write lock.
 * The write lock is exclusive, meaning only one thread can hold it at a time, and no other
 * threads can hold read or write locks while a thread holds the write lock.
 */
public class StampedReadWriteLockExample {

    public void produce(StampedLock stampedLock) {
        long stamp = stampedLock.readLock();
        System.out.println("Read lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate read operation
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockRead(stamp);
            System.out.println("Read lock released by thread: " + Thread.currentThread().getName());
        }
    }

    public void consume(StampedLock stampedLock) {
        System.out.println("Attempting to acquire write lock by thread: " + Thread.currentThread().getName());
        long stamp = stampedLock.writeLock();
        System.out.println("Write lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate write operation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stampedLock.unlockWrite(stamp);
            System.out.println("Write lock released by thread: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StampedReadWriteLockExample example = new StampedReadWriteLockExample();
        StampedLock stampedLock = new StampedLock();

        Thread reader1 = new Thread(() -> {
            example.produce(stampedLock);
        }, "Reader-1");

        Thread reader2 = new Thread(() -> {
            example.produce(stampedLock);
        }, "Reader-2");

        Thread writer = new Thread(() -> {
            example.consume(stampedLock);
        }, "Writer-1");

        reader1.start();
        reader2.start();
        Thread.sleep(100); // Ensure readers start before writer
        writer.start();
    }
}
