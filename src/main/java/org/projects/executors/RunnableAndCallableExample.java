package org.projects.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This example demonstrates the differences between Runnable and Callable tasks when submitted to a ThreadPoolExecutor.
 * It shows how Future behaves differently based on whether the task is a Runnable or a Callable.
 * Runnable tasks do not return a result, while Callable tasks do.
 * 1. When a Runnable is submitted, Future.get() returns null since Runnable does not produce a result.
 * 2. When a Runnable with a specified result is submitted, Future.get() returns that result after execution.
 * 3. When a Callable is submitted, Future.get() returns the value produced by the Callable.
 * This example highlights how to handle tasks with and without return values in a concurrent environment.
 */
public class RunnableAndCallableExample {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 60, java.util.concurrent.TimeUnit.SECONDS,
                new java.util.concurrent.ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        // Runnable has return type void, so Future.get() returns null
        Future<?> future1 = executor.submit(() -> {
            System.out.println("Runnable Task executed by " + Thread.currentThread().getName());
        });

        try {
            Object object1 = future1.get();
            System.out.println("Future from Runnable returns: " + object1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Runnable with result, Future.get() returns the provided result
        List<Integer> sharedList = new ArrayList<>();
        Future<List<Integer>> future2 = executor.submit(() -> {
            sharedList.add(1);
        }, sharedList);

        try {
            List<Integer> resultList = future2.get();
            System.out.println("Future from Runnable with result returns: " + resultList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Callable returns a value, Future.get() returns that value
        Future<List<Integer>> future3 = executor.submit(() -> {
            return List.of(1, 2, 3);
        });

        try {
            List<Integer> result = future3.get();
            System.out.println("Future from Callable returns: " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
