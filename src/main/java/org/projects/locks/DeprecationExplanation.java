package org.projects.locks;

/**
 * DeprecationExplanation
 * <p>
 * This class is a documentation-holder (and light runnable example) explaining why
 * the Thread methods {@code stop()} and {@code suspend()}/{@code resume()} were
 * deprecated in the Java platform. It contains:
 *
 * - Detailed explanation of the problems caused by these methods.
 * - Short (commented) code snippets that illustrate the issues.
 * - Safe, cooperative alternatives that should be used instead.
 *
 * Notes:
 * - The problematic examples are shown as comments so they are visible for
 *   study without being executed. The safe alternatives are small runnable
 *   patterns (cooperative stop and cooperative pause) included below.
 * - This file is intended as educational material you can open in the IDE.
 */
public class DeprecationExplanation {

    /**
     * Short summary printed by main.
     */
    public static void main(String[] args) {
        System.out.println("Why Thread.stop() and Thread.suspend()/resume() were deprecated:\n");
        System.out.println("1) Thread.stop(): asynchronous, unsafe termination that can leave shared data in an inconsistent state,\n" +
                "   can abort a thread while it holds locks or is updating invariants, and interacts poorly with native code and finally blocks.\n");
        System.out.println("2) Thread.suspend()/resume(): suspending a thread while it holds locks can deadlock other threads; resume/suspend can also be lost/timing-dependent.\n");
        System.out.println("Use cooperative cancellation (interrupt or volatile flags) and higher-level concurrency utilities instead.\n");

        System.out.println("See the source comments in this class for detailed explanations and examples.");
    }

    /*
     * ----------------------------
     * Why Thread.stop() is dangerous
     * ----------------------------
     *
     * Summary:
     *  - Thread.stop() asynchronously throws a ThreadDeath in the target thread at an arbitrary point.
     *  - If the target thread is in the middle of updating shared data, or holds locks, the program may observe broken invariants.
     *  - Although finally blocks execute for ThreadDeath in most pure-Java code, native code, VM-internal actions, or complex interactions
     *    can still leave resources or invariants broken.
     *  - ThreadDeath can be caught by user code; if a library catches it and does not rethrow, the intended termination is defeated.
     *
     * Example problem (illustrative, do not run):
     *
     * // BAD: demonstrates invariant breakage if thread is stopped between updates
     * class Shared {
     *     int a = 100;
     *     int b = 0;
     *     synchronized void update() {
     *         a = 0;              // partial update
     *         try { Thread.sleep(200); } catch (InterruptedException ignored) {}
     *         b = 100;            // invariant restored here
     *     }
     *     synchronized int sum() { return a + b; }
     * }
     *
     * If another thread calls writer.stop() while the writer is sleeping, sum() may observe 0 + 0 == 0 which breaks the invariant a + b == 100.
     *
     * Key consequences:
     *  - Data corruption and broken invariants
     *  - Resource leaks because cleanup may not run in all contexts
     *  - Unpredictable interaction with native libraries and the JVM
     *
     * Safer alternatives:
     *  - Cooperative cancellation using a volatile flag (for non-blocking work)
     *  - Interrupts (Thread.interrupt()) for tasks that block on interruptible operations
     *  - Higher-level constructs (ExecutorService + Future.cancel(), java.util.concurrent classes)
     */

    /*
     * -------------------------------
     * Why Thread.suspend()/resume() are dangerous
     * -------------------------------
     *
     * Summary:
     *  - suspend() freezes a thread at an arbitrary point. If the suspended thread holds a monitor (lock) or other resource,
     *    other threads waiting for that resource may block forever, causing deadlocks.
     *  - resume() may be called before suspend() in some interleavings leading to lost resumes (the thread stays suspended).
     *  - Because suspend/resume act from outside the target thread, the target cannot cleanly reach a safe suspension point.
     *
     * Example problem (illustrative, do not run):
     *
     * Object LOCK = new Object();
     *
     * Thread t = new Thread(() -> {
     *     synchronized(LOCK) {
     *         // t holds LOCK for some time
     *         doSomething();
     *     }
     * });
     * t.start();
     * // If we call t.suspend() while t is inside the synchronized block above,
     * // t will be suspended while holding LOCK. Any other thread trying to enter
     * // synchronized(LOCK) will block forever (deadlock risk).
     *
     * Safer alternative:
     *  - Design threads to check a volatile pause flag and wait() on a dedicated monitor only at safe points where they do not hold critical locks.
     *  - Use higher-level constructs like Phaser, CountDownLatch, or custom Condition objects from ReentrantLock.
     */

    /*
     * ----------------------------
     * Runnable safe alternatives
     * ----------------------------
     *
     * Below are two tiny cooperating patterns you can copy into code: a cooperative stop (volatile flag) and a cooperative pause (wait/notify).
     * Both avoid forcibly controlling another thread from the outside and let the thread reach safe points to release locks and cleanup.
     */

    /**
     * Cooperative stop: the worker checks a volatile flag and exits cleanly when asked to stop.
     * This is suitable when the worker does non-blocking work or occasionally checks the flag.
     */
    public static class CooperativeStop implements Runnable {
        private volatile boolean running = true;

        public void stop() { running = false; }

        @Override
        public void run() {
            try {
                while (running) {
                    // do a unit of work
                    // ensure expensive operations or critical sections are bounded
                    workUnit();
                }
            } finally {
                // cleanup here; guaranteed to run when the loop exits cooperatively
                cleanup();
            }
        }

        private void workUnit() {
            // placeholder
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        private void cleanup() {
            // release resources
        }
    }

    /**
     * Cooperative pause: the worker waits on a dedicated monitor at safe points. The pause state is controlled via a volatile flag and
     * notify/notifyAll on the PAUSE_LOCK. Critical resources should not be held while waiting on PAUSE_LOCK.
     */
    public static class CooperativePause implements Runnable {
        private volatile boolean paused = false;
        private final Object PAUSE_LOCK = new Object();

        public void requestPause() {
            paused = true;
        }

        public void resumeFromPause() {
            synchronized (PAUSE_LOCK) {
                paused = false;
                PAUSE_LOCK.notifyAll();
            }
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    // acquire and use resources only in short critical sections
                    useResource();

                    // safe pause point: do not hold important locks while waiting
                    synchronized (PAUSE_LOCK) {
                        while (paused) {
                            try { PAUSE_LOCK.wait(); } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                }
            } finally {
                // cleanup
            }
        }

        private void useResource() {
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        }
    }

}

