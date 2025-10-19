package org.projects.thread;

public class ThreadExample2 {
    // Creating a thread by implementing the Runnable interface
    public static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("MyRunnable is running");
            System.out.println("MyRunnable finished execution");
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}
