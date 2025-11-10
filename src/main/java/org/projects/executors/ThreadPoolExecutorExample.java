package org.projects.executors;

import java.util.concurrent.*;

/**
 * This example demonstrates the behavior of ThreadPoolExecutor with custom ThreadFactory and RejectedExecutionHandler.
 * It shows three scenarios:
 * 1. Tasks are accepted and no threads other than core threads are created as the queue has space.
 * 2. Tasks are accepted and new threads are created up to maximum pool size as the queue is full.
 * 3. Tasks are rejected as both the queue and maximum pool size are reached.
 * The threadPoolExecutor is better than creating new threads for each task as it reuses existing threads, reducing overhead.
 * This improves performance, especially in applications with a large number of short-lived tasks. It also manages the
 * thread lifecycle, helping to avoid resource exhaustion.
 * Maximum pool size is the upper limit on the number of threads that can be created by the ThreadPoolExecutor. This happens
 * when the work queue is full and new tasks are submitted. The executor will create new threads up to this limit to handle
 * the incoming tasks. Once the maximum pool size is reached, any additional tasks will be handled according to the
 * RejectedExecutionHandler policy. There are Rejection policies like AbortPolicy, CallerRunsPolicy, DiscardOldestPolicy,
 * and DiscardPolicy. Custom policies can also be implemented by implementing the RejectedExecutionHandler interface.
 * KeepAlive time is the amount of time that excess idle threads (threads that are not core threads) will wait for new tasks
 * before terminating. This helps to reduce resource consumption when the demand for threads decreases. If a thread has been
 * idle for longer than the keepAlive time, it will be terminated and removed from the pool.
 */
public class ThreadPoolExecutorExample {

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2), new CustomThreadFactory(), new CustomRejectedExecutionHandler());

        Runnable runnable = () -> {
            long time = System.currentTimeMillis();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Completed Task created on " + time + " by " + Thread.currentThread().getName());
        };

        System.out.println("Scenario where tasks are accepted and no threads other than core threads are created as queue has space");
        for (int i = 1; i <= 4; i++) {
            Thread.sleep(100);
            executor.submit(runnable);
        }

        Thread.sleep(7000);

        System.out.println("\nScenario where tasks are accepted and new threads are created up to maximum pool size as queue is full");
        for (int i = 1; i <= 5; i++) {
            Thread.sleep(100);
            executor.submit(runnable);
        }

        Thread.sleep(7000);

        System.out.println("\nScenario where tasks are rejected as both queue and maximum pool size are reached");
        for (int i = 1; i <= 8; i++) {
            Thread.sleep(100);
            executor.submit(runnable);
        }

        executor.shutdown();
    }

}

class CustomThreadFactory implements ThreadFactory {

    int threadCount = 0;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("CustomThread-" + threadCount++);
        return thread;
    }
}

class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("Task " + r.toString() + " rejected from " + executor.toString());
    }
}