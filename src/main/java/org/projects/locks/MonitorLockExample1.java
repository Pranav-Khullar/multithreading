package org.projects.locks;

/**
 * This class demonstrates the use of monitor locks in Java.
 * It contains three methods: task1, task2, and task3.
 * - task1 is synchronized, meaning it acquires the monitor lock of the instance when executed.
 * - task2 has a synchronized block that also acquires the monitor lock of the instance.
 * - task3 is a regular method without any synchronization.
 *
 * When multiple threads attempt to execute these methods on the same instance,
 * the monitor lock ensures that only one thread can execute a synchronized method or block at a time.
 * Monitor locks are at an instance level, so different instances have different locks but the same instance shares the same lock.
 * So that means different methods synchronized on the same instance will block each other.
 * As in the example, if task1 is being executed by one thread, any other thread trying to execute task1 or the synchronized block in task2
 * will be blocked until task1 completes.
 */
public class MonitorLockExample1 {
    public synchronized void task1() {
        System.out.println("Task 1 started");
        try {
            for(int i = 1; i <= 10; i++) {
                System.out.println("Task 1 - Count: " + i + " thread: " + Thread.currentThread().getName());
                Thread.sleep(500); // Simulating work with sleep
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Task 1 finished");
    }

    public void task2() {
        System.out.println("Task 2 outside synchronized block by thread: " + Thread.currentThread().getName());
        synchronized (this) {
            System.out.println("Task 2 inside synchronized block started by thread: " + Thread.currentThread().getName());
            try {
                for(int i = 1; i <= 6; i++) {
                    System.out.println("Task 2 - Count: " + i + " thread: " + Thread.currentThread().getName());
                    Thread.sleep(500); // Simulating work with sleep
                } // Simulating work with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Task 2 finished by thread: " + Thread.currentThread().getName());
    }

    public void task3() {
        System.out.println("Task 3 started");
        System.out.println("Task 3 finished");
    }

    public static void main(String[] args) {
        MonitorLockExample1 example = new MonitorLockExample1();

        Thread thread1 = new Thread(() -> example.task1());
        Thread thread2 = new Thread(() -> example.task2());
        Thread thread3 = new Thread(() -> example.task3());

        thread1.start();
        thread2.start();
        thread3.start();

        // using runnable implementation for creating a thread
        MyRunnable myRunnable = new MyRunnable(example);
        Thread thread4 = new Thread(myRunnable, "MyRunnable-Thread");
        thread4.start();
    }

    public static class MyRunnable implements Runnable {

        MonitorLockExample1 monitorLockExample1;
        public MyRunnable(MonitorLockExample1 monitorLockExample1) {
            this.monitorLockExample1 = monitorLockExample1;
        }

        @Override
        public void run() {
            monitorLockExample1.task2();
        }
    }
}
