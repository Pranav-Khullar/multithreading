package org.projects.thread;

public class ThreadExample5 {
    // Accessing the current thread's name within a runnable lambda expression
    public static void main(String[] args) {
        Runnable runnable = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " is running");
            System.out.println(threadName + " finished execution");
        };

        // Creating a thread with a custom name
        Thread thread = new Thread(runnable, "CustomThread-1");
        thread.start();
    }
}
