package net.cacpixel.rtmmetro.rail.util.construct;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.util.ModLog;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskLoopThread extends Thread {
    private static final TaskLoopThread INSTANCE = new TaskLoopThread();
    private boolean loop = true;
    private final ConcurrentLinkedQueue<RailConstructTask> taskQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService pool;

    public TaskLoopThread() {
        super("TaskLoopThread");
    }

    public static TaskLoopThread getInstance() {
        if (!INSTANCE.isAlive()) {
            INSTANCE.loop = true;
            INSTANCE.start();
        }
        return INSTANCE;
    }

    public void init() {
        this.pool = Executors.newFixedThreadPool(ModConfig.threadsToConstructRails <= 0 ?
                        Runtime.getRuntime().availableProcessors() * 2
                        : Math.min(ModConfig.threadsToConstructRails, Runtime.getRuntime().availableProcessors() * 2),
                new ThreadFactory() {
                    private final AtomicInteger num = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable runnable) {
                        ThreadGroup group = System.getSecurityManager().getThreadGroup();
                        Thread thread = new Thread(group, runnable,
                                "Rail Construct Thread " + this.num.getAndIncrement());
                        thread.setPriority(NORM_PRIORITY);
                        thread.setDaemon(false);
                        return thread;
                    }
                });
    }

    @Override
    public void run() {
        this.init();
        while (this.loop) {
            RailConstructTask task = this.taskQueue.poll();
            if (task != null) {
                this.pool.submit(() -> {
                    constructTask(task);
                });
            } else {
                this.waitForTask();
            }
        }
    }

    public void waitForTask() {
        try {
            this.join();
            ModLog.info("Waiting for tasks...");
        } catch (InterruptedException ignored) {
            ModLog.info("Task start Running...");
        }
    }

    public static void constructTask(RailConstructTask task) {
        try {
            task.runTask();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            task.stopTask();
        }
    }

    public void endLoop() {
        this.loop = false;
        this.interrupt();
    }

    public ConcurrentLinkedQueue<RailConstructTask> getQueue() {
        return this.taskQueue;
    }

    public void addTask(RailConstructTask task) {
        this.getQueue().add(task);
        this.interrupt();
    }

    public void addTask(Iterable<RailConstructTask> tasks) {
        tasks.forEach(task -> this.addTask(task));
    }
}
