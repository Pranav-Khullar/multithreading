package org.projects.executors;

import java.util.concurrent.*;

/**
 * This example demonstrates the use of CompletableFuture for asynchronous programming in Java.
 * It showcases various features of CompletableFuture including supplyAsync, thenApply, thenApplyAsync,
 * thenComposeAsync, thenAccept, and thenCombineAsync.
 * CompletableFuture allows for non-blocking operations and chaining of asynchronous tasks,
 * making it easier to write concurrent code.
 * Each stage of the CompletableFuture can run in different threads, allowing for efficient use of resources.
 * <br>
 * The Async suffix in the method thenApplyAsync means that the thread completing the future will not be blocked by the
 * execution of the Consumer#accept(T t) method.
 * The usage of thenApplyAsync vs thenApply depends if you want to block the thread completing the future or not.
 * <br>
 * thenComposeAsync is used to chain two CompletableFutures where the second depends on the result of the first.
 * It flattens nested CompletableFutures into a single one.
 * Difference between thenComposeAsync and thenApplyAsync is that thenComposeAsync is used when the function returns a CompletableFuture,
 * while thenApplyAsync is used when the function returns a direct value. This means that thenComposeAsync is useful for chaining
 * asynchronous operations that depend on each other, while thenApplyAsync is used for transforming the result of a single asynchronous operation.
 * So basically, thenComposeAsync is for dependent async tasks, and thenApplyAsync is for independent transformations.
 * For more information, refer to
 * <a href="https://stackoverflow.com/questions/30212335/what-is-the-difference-between-thenapply-and-thencompose-in-completablefuture">thenApply vs thenCompose</a>
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        /*
          supplyAsync: Used to run a task asynchronously that returns a result.
         */
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executing supply async alone in thread: " + Thread.currentThread().getName());
            return "Hello from CompletableFuture!\n";
        }, threadPoolExecutor);

        try {
            System.out.println(completableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*
          thenApply: Transforms the result of a CompletableFuture when it completes.
          Runs in the same thread as the previous stage.
         */
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executing supply async in thread: " + Thread.currentThread().getName());
            return "Hello from ";
        }, threadPoolExecutor).thenApply((String s) -> {
            System.out.println("Executing then apply in thread: " + Thread.currentThread().getName());
            return s + "CompletableFuture! with thenApply.\n";
        });

        try {
            System.out.println(completableFuture1.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*
          thenApplyAsync: Similar to thenApply but runs the transformation in a different thread.
          This is useful for offloading work to another thread pool and avoiding blocking the current thread.
          By default, it uses the ForkJoinPool.commonPool() if no executor is provided.
         */
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executing supply async in thread: " + Thread.currentThread().getName());
            return System.currentTimeMillis();
        }, threadPoolExecutor).thenApplyAsync((Long curTime) -> {
            System.out.println("Executing then apply async in thread: " + Thread.currentThread().getName());
            return "Current Time in millis: " + curTime + " from CompletableFuture with thenApplyAsync.\n";
        });

        try {
            System.out.println(completableFuture2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*
          thenComposeAsync: Used to chain two CompletableFutures where the second depends on the result of the first.
          It flattens nested CompletableFutures into a single one.
         */
        CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executing supply async in thread: " + Thread.currentThread().getName());
            return "Hello from ";
        }, threadPoolExecutor).thenComposeAsync((String s) -> {
            System.out.println("Executing then compose in thread: " + Thread.currentThread().getName());
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing inner supply async in thread: " + Thread.currentThread().getName());
                return s + "CompletableFuture! with thenCompose.\n";
            }, threadPoolExecutor);
        }, threadPoolExecutor);

        try {
            System.out.println(completableFuture3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*
          thenAccept: Consumes the result of a CompletableFuture when it completes.
          It does not return a new value.
         */
        CompletableFuture<Integer> completableFuture4 = CompletableFuture.supplyAsync(() -> {
            return 0;
        });
        CompletableFuture<Void> completableFuture5 = completableFuture4.thenApplyAsync((Integer val) -> {
            System.out.println("Executing first then apply in thread: " + Thread.currentThread().getName());
            for(int i=0; i<1000; i++) {
                val += 1;
            }
            return val;
        }, threadPoolExecutor).thenAccept((Integer val) -> {
            System.out.println("Executing then accept in thread: " + Thread.currentThread().getName());
            System.out.println("Final value after chained operations: " + val + "\n");
        });


        CompletableFuture<Integer> completableFutureInteger = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<String> completableFutureString = CompletableFuture.supplyAsync(() -> " K steps is the goal.");

        /*
          thenCombineAsync: Combines the results of two independent CompletableFutures when both complete.
          It takes a BiFunction that defines how to combine the results.
         */
        CompletableFuture<String> combinedFuture = completableFutureString.thenCombineAsync(completableFutureInteger, (String message, Integer steps) -> {
            System.out.println("Combining results in thread: " + Thread.currentThread().getName());
            return steps + message + "\n";
        }, threadPoolExecutor);

        try {
            System.out.println(combinedFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        threadPoolExecutor.shutdown();
    }
}
