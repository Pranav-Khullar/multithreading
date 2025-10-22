package org.projects.locks;

import static java.lang.Thread.sleep;

/**
 * This example demonstrates the use of monitor locks in Java using synchronized methods,
 * along with wait() and notifyAll() for inter-thread communication.
 * It shows how wait releases the monitor lock and how notifyAll wakes up waiting threads.
 * A consumer thread waits for an item to be available, and a producer thread adds the item after a delay.
 */
public class MonitorLockWaitExample {

    public static class SharedResource {
        boolean itemAvailable = false;

        public synchronized void addItem() {
            itemAvailable = true;
            System.out.println("Item added by thread: " + Thread.currentThread().getName());
            notifyAll(); // Notify waiting threads that an item is available
        }

        public synchronized void consumeTask() {
            System.out.println("Consume task invoked by thread: " + Thread.currentThread().getName());

            // while loop to handle spurious wakeups(function resuming even though notify was not called),
            // since after being notified, the execution continues from the
            // wait() call, so if we use if condition, it wont be checked again and may lead to incorrect behavior
            // whereas if while is used, the condition is re-evaluated after being notified or spurious wakeup
            while(!itemAvailable) {
                try {
                    System.out.println("Item not available, thread " + Thread.currentThread().getName() + " is waiting.");
                    wait(); // Wait until notified that an item is available, this releases the monitor lock
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                sleep(1000); // Simulate some processing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Item consumed by thread: " + Thread.currentThread().getName());
            itemAvailable = false; // Reset the flag after consuming the item
        }
    }

    public static void main(String[] args) {
        SharedResource sharedResource = new SharedResource();

        Thread consumerThread = new Thread(() -> {
            sharedResource.consumeTask();
        }, "ConsumerThread");

        Thread producerThread = new Thread(() -> {
            try {
                sleep(5000);
                sharedResource.addItem(); // Simulate some delay before adding an item
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "ProducerThread");

        producerThread.start();
        consumerThread.start();
    }
}
