package org.projects.thread;

public class ThreadExample1 {
    // Creating a thread by extending the Thread class
    public static class MyThread extends Thread {
        public void run() {
            System.out.println("MyThread is running");
            System.out.println("MyThread finished execution");
        }
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
    }
}
