package org.projects.customLocks;

import java.util.concurrent.locks.ReentrantLock;

/**
 * This example demonstrates the use of ReentrantLock for thread synchronization.
 * It is used even when multiple instances of the class are created.
 * The ReentrantLock is shared among the instances to ensure that only one thread
 * can execute the produce method at a time, regardless of the instance.
 * For multiple instances, synchronized methods would not work as each instance has its own monitor lock.
 */
public class ReentrantLockExample {

    public void produce(ReentrantLock lock) throws InterruptedException {
        lock.lock();
        System.out.println("Lock acquired by thread: " + Thread.currentThread().getName());
        Thread.sleep(2000);

        System.out.println("Lock released by thread: " + Thread.currentThread().getName());
        lock.unlock();
    }

    public static void main(String[] args) {

        ReentrantLockExample example1 = new ReentrantLockExample();
        ReentrantLockExample example2 = new ReentrantLockExample();

        ReentrantLock lock = new ReentrantLock();

        Thread thread1 = new Thread(() -> {
            try {
                example1.produce(lock);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                example2.produce(lock);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
    }
}
