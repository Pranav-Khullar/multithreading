package org.projects.thread;

import static java.lang.Thread.sleep;

public class ThreadExample9 {
    /**
     * Main method to demonstrate thread behavior with join
     * If a thread calls join on another thread, the calling thread will wait for the called thread to finish
     * We can also pass a timeout to the join method, causing the calling thread to wait only for the specified time
     * If the thread doesn't finish in that time, the calling thread resumes execution
     * @param args
     */
    public static void main(String[] args) {
        Runnable runnable = () -> {
            for(int i=0; i<5; i++) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Thread running...");
            }
        };

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();

        try {
            // main thread calls join on the daemon thread
            // this blocks the main thread until the daemon thread completes its execution
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
