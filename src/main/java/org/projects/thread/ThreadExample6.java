package org.projects.thread;

public class ThreadExample6 {

    public static void main(String[] args) {
        // Creating a runnable with sleep to simulate work
        Runnable runnable = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " is running");

            try {
                Thread.sleep(2000); // Simulate some work with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(threadName + " finished execution");
        };

        Thread thread = new Thread(runnable, "CustomThread-2");
        thread.start();
    }
}
