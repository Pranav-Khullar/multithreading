package org.projects.thread;

import static java.lang.Thread.sleep;

public class ThreadExample8 {
    /**
     * Creating a daemon thread that runs indefinitely, but since daemon threads do not prevent the JVM from exiting,
     * the program will terminate after the main thread completes its execution.
     * so daemon threads are typically used for background tasks that should not block the application from shutting down.
     * daemon threads are useful for tasks like garbage collection, logging, background monitoring, or other low-priority tasks
     * they should not do critical work that must be completed before the application exits.
     **/
    public static void main(String[] args) {
        Runnable runnable = () -> {
            while(true){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Thread is running");
            }
        };

        Thread thread = new Thread(runnable, "InfiniteThread");
        thread.setDaemon(true); // Set the thread as a daemon thread
        thread.start();
        try {
            sleep(3200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
