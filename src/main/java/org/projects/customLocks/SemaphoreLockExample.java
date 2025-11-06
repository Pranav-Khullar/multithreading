package org.projects.customLocks;

import java.util.concurrent.Semaphore;

/**
 * This example demonstrates the use of Semaphore for controlling access to a resource.
 * The Semaphore is initialized with a fixed number of permits, allowing multiple threads
 * to access the resource concurrently up to the limit of permits.
 */
public class SemaphoreLockExample {

    Semaphore semaphore = new Semaphore(2); // Allowing 2 permits

    public void accessResource() {
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " is attempting to acquire a permit.");
            semaphore.acquire();
            System.out.println("Thread " + Thread.currentThread().getName() + " has acquired a permit.");

            // Simulate resource access
            System.out.println("Thread " + Thread.currentThread().getName() + " is accessing the resource.");
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            System.out.println("Thread " + Thread.currentThread().getName() + " has released a permit.");
        }
    }

    public static void main(String[] args) {
        SemaphoreLockExample example = new SemaphoreLockExample();

        Runnable task = () -> {
            example.accessResource();
        };

        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");
        Thread thread3 = new Thread(task, "Thread-3");
        Thread thread4 = new Thread(task, "Thread-4");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
