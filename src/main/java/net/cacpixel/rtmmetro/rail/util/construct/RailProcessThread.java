package net.cacpixel.rtmmetro.rail.util.construct;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.rail.util.RailMapAdvanced;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RailProcessThread extends Thread {
    private static final RailProcessThread INSTANCE = new RailProcessThread();
    private boolean loop = true;
    private final RailConstructTaskQueue taskQueue = new RailConstructTaskQueue();
    private ExecutorService pool;
    private List<Future<?>> futures = new ArrayList<>();

    public RailProcessThread() {
        super("Rail Process Thread");
    }

    public static RailProcessThread getInstance() {
        if (!INSTANCE.isAlive()) {
            INSTANCE.start();
        }
        return INSTANCE;
    }

    public void init() {
        if ((ModConfig.threadsToConstructRails <= 0)) {
            this.pool = Executors.newWorkStealingPool();
        } else {
            this.pool = Executors.newFixedThreadPool(Math.max(ModConfig.threadsToConstructRails, Runtime.getRuntime().availableProcessors()),
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
    }

    @Override
    public void run() {
        this.init();
        while (this.loop) {
            RailConstructTask task = this.taskQueue.poll();
            if (task != null) {
                this.futures.add(this.pool.submit(() -> {
                    constructTask(task);
                }));
            } else {
                this.waitForTask();
            }
        }
    }

    public void waitForTask() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
            ;
        }
    }

    public static void constructTask(RailConstructTask task) {
        try {
            task.processed = false;
            task.runTask();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            task.processed = true;
        }
    }

    public void endLoop() {
        this.loop = false;
    }

    public RailConstructTaskQueue getQueue() {
        return this.taskQueue;
    }

    public void addTask(RailConstructTask task) {
        task.processed = false;
        this.getQueue().add(task);
    }

    public void addTaskList(Iterable<RailConstructTask> tasks) {
        tasks.forEach(task -> this.getQueue().add(task));
    }
}
