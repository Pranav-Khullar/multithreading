package org.projects.customLocks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This example demonstrates the use of ReadWriteLock for thread synchronization.
 * The read lock is shared, allowing multiple threads to read simultaneously.
 * Multiple threads can hold read locks simultaneously as long as no thread holds the write lock.
 * The write lock is exclusive, meaning only one thread can hold it at a time, and no other
 * threads can hold read or write locks while a thread holds the write lock.
 */
public class ReadWriteLockExample {

    public void produce(ReadWriteLock readWriteLock) {
        readWriteLock.readLock().lock();
        System.out.println("Read lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate read operation
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
            System.out.println("Read lock released by thread: " + Thread.currentThread().getName());
        }
    }

    public void consume(ReadWriteLock readWriteLock) {
        readWriteLock.writeLock().lock();
        System.out.println("Write lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate write operation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
            System.out.println("Write lock released by thread: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        ReadWriteLockExample example = new ReadWriteLockExample();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        Thread reader1 = new Thread(() -> {
            example.produce(readWriteLock);
        }, "Reader-1");

        Thread reader2 = new Thread(() -> {
            example.produce(readWriteLock);
        }, "Reader-2");

        Thread writer = new Thread(() -> {
            example.consume(readWriteLock);
        }, "Writer-1");

        reader1.start();
        reader2.start();
        writer.start();
    }
}
