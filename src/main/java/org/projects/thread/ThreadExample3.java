package org.projects.thread;

public class ThreadExample3 {
    // Creating a thread using an anonymous Runnable implementation
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable is running");
                System.out.println("Runnable finished execution");
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}
