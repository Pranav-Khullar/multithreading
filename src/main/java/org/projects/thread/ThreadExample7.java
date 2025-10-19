package org.projects.thread;

public class ThreadExample7 {

    // Creating a stoppable thread by implementing the Runnable interface
    public static class StoppableRunnable implements Runnable {

        public void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean stopRequested = false;

        public synchronized void requestStop() {
            this.stopRequested = true;
        }

        public synchronized boolean isStopRequested() {
            return this.stopRequested;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " is running");

            while(!isStopRequested()) {
                sleep(1000);
                System.out.println("...");
            }

            System.out.println(threadName + " finished execution");
        }
    }

    public static void main(String[] args) {
        StoppableRunnable stoppableRunnable = new StoppableRunnable();
        Thread thread = new Thread(stoppableRunnable, "StoppableThread-1");
        Thread thread2 = new Thread(stoppableRunnable, "StoppableThread-2");
        thread.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Requesting stop...");
        stoppableRunnable.requestStop();
        System.out.println("Stop requested.");
        thread2.start();
    }
}
