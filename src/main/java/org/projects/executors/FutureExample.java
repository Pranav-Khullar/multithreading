package org.projects.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

/**
 * This example demonstrates the use of Future to monitor the status of a submitted task in a ThreadPoolExecutor.
 * It shows how to check if the task is completed, handle timeouts when waiting for the task to complete,
 * and retrieve the final status of the task.
 * Future is useful for tracking the progress of asynchronous tasks and retrieving their results once they are done.
 * It provides methods to check if the task is completed, wait for its completion, and cancel it if needed.
 * Future.get() blocks the calling thread until the task is completed, while Future.get(timeout) allows specifying
 * a maximum wait time, throwing a TimeoutException if the task is not completed within that time
 */
public class FutureExample {

    public static void main(String[] args) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60, java.util.concurrent.TimeUnit.SECONDS,
                new java.util.concurrent.ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        Future<?> future = executor.submit(() -> {
            try {
                Thread.sleep(7000);
                System.out.println("Task Completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Is the task completed? " + future.isDone());
        try {
            future.get(2, java.util.concurrent.TimeUnit.SECONDS);
        } catch (TimeoutException exception) {
            System.out.println("Task is still not completed, timeout occurred.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            future.get(); // Waits until the task is completed
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Is the task completed? " + future.isDone());
        System.out.println("Is the task cancelled? " + future.isCancelled());

        executor.shutdown();
    }
}
