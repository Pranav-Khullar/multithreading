package org.projects.customLocks;

import java.util.concurrent.locks.StampedLock;

/**
 * This example demonstrates the use of StampedLock with optimistic reading for thread synchronization.
 * The optimistic read lock allows multiple threads to read simultaneously without blocking,
 * but if a write occurs during the read, the optimistic read fails and the thread must acquire
 * a proper read lock to ensure data consistency.
 * -----
 * An optimistic read with StampedLock starts by using tryOptimisticRead() to get a stamp without blocking.
 * The shared variables are copied to local variables immediately after. The thread then calls validate(stamp) to check
 * if a write lock was acquired in the meantime. If validation succeeds, the local variables are used.
 * If it fails, a pessimistic read lock is acquired to re-read the variables, and the read lock is released after the operation.
 * -----
 * The write lock is exclusive, meaning only one thread can hold it at a time, and no other
 * threads can hold read or write locks while a thread holds the write lock.
 * -----
 * Though optimistic writes are not supported by stamped locks, still some theory for it is that we can use compare-and-swap (CAS)
 * operations to implement optimistic writes. The idea is to read the current value of a variable,
 * perform the necessary modifications, and then use a CAS operation to update the variable only if it
 * has not been changed by another thread in the meantime. If the CAS operation fails, the thread can retry the operation.
 */
public class StampedOptimisticReadLockExample {

    int value = 0;
    StampedLock stampedLock = new StampedLock();

    public void read() {
        long stamp = stampedLock.tryOptimisticRead();
        System.out.println("Optimistic read lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate read operation
        try {
            Thread.sleep(2500);
            if (!stampedLock.validate(stamp)) {
                System.out.println("Optimistic read failed, acquiring read lock by thread: " + Thread.currentThread().getName());
                stamp = stampedLock.readLock();
                try {
                    // Perform read operation under read lock
                    Thread.sleep(1000);
                } finally {
                    stampedLock.unlockRead(stamp);
                    System.out.println("Read lock released by thread: " + Thread.currentThread().getName() + ", value: " + value);
                }
            } else {
                System.out.println("Optimistic read successful by thread: " + Thread.currentThread().getName() + ", value: " + value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void write(int a) {
        long stamp = stampedLock.writeLock();
        System.out.println("Write lock acquired by thread: " + Thread.currentThread().getName());
        // Simulate write operation
        try {
            Thread.sleep(2000);
            value = a;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            stampedLock.unlockWrite(stamp);
            System.out.println("Write lock released by thread: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StampedOptimisticReadLockExample example = new StampedOptimisticReadLockExample();

        Thread reader1 = new Thread(() -> {
            example.read();
        }, "Reader-1");

        Thread reader2 = new Thread(() -> {
            example.read();
        }, "Reader-2");

        Thread writer = new Thread(() -> {
            example.write(42);
        }, "Writer-1");

        reader1.start();

        Thread.sleep(5000); // Ensure first reader starts before second reader
        // scenario where lock fails since write happens during read
        reader2.start();
        writer.start();
    }
}
