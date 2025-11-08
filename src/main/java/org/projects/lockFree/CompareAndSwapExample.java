package org.projects.lockFree;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This example demonstrates the use of AtomicInteger for thread-safe operations without using locks.
 * It compares three methods of incrementing a counter: a thread-unsafe method, a synchronized method,
 * and an atomic method using AtomicInteger. The atomic method ensures that increments are done safely
 * in a concurrent environment without the overhead of synchronization.
 * The thread-unsafe method may lead to incorrect results due to race conditions.
 * The synchronized method ensures correctness but may have performance overhead due to locking.
 * The atomic method provides a lock-free way to achieve thread safety. It uses low-level atomic CPU instructions
 * to perform operations atomically. CAS (Compare-And-Swap) is a common atomic operation used in such scenarios.
 * In CAS, the current value is compared with an expected value, and if they match, it is updated to a new value.
 * This operation is performed atomically, ensuring that no other thread can modify the value in between.
 * CAS gets the volatile value from memory, compares it with the expected value, and if they are the same,
 * it updates the value to the new value. Volatile basically means that the variable is stored in main memory,
 * and reads/writes to it are directly done in main memory, ensuring visibility across threads as it is possible
 * that each thread may have its own CPU cache and may make changes to the cached value which may not be visible to other threads.
 * The changes to the cached value may not be immediately reflected in the main memory, leading to inconsistencies as these
 * changes are synchronized back to main memory at some later point in time. So volatile ensures that the variable is always
 * read from and written to main memory, ensuring visibility of changes across threads.
 */
public class CompareAndSwapExample {
    private int counter = 0;
    private AtomicInteger atomicCounter = new AtomicInteger(0);

    public void threadUnsafeIncrement() {
        counter++;
    }

    public synchronized void synchronizedIncrement() {
        counter++;
    }

    public void atomicIncrement() {
        atomicCounter.getAndIncrement();
    }

    public int getCounter() {
        return counter;
    }

    public void resetCounter() {
        counter = 0;
    }

    public int getAtomicCounter() {
        return atomicCounter.get();
    }

    public static void main(String[] args) {
        CompareAndSwapExample example = new CompareAndSwapExample();
        int numberOfThreads = 100;
        int incrementsPerThread = 1000;

        Thread[] threads = new Thread[numberOfThreads];

        // Testing thread-unsafe increment
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.threadUnsafeIncrement();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final counter value (thread-unsafe): " + example.getCounter());

        // Reset counter for synchronized test
        example.resetCounter();

        // Testing synchronized increment
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.synchronizedIncrement();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final counter value (synchronized): " + example.getCounter());

        // Reset atomic counter for atomic test
        example.atomicCounter.set(0);

        // Testing atomic increment
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.atomicIncrement();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final atomic counter value: " + example.getAtomicCounter());
    }
}
