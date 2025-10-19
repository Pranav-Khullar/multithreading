package org.projects.thread;

public class ThreadExample4 {
    // Creating a thread using a lambda expression
    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println("Lambda Runnable is running");
            System.out.println("Lambda Runnable finished execution");
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}
