package net.cacpixel.rtmmetro.rail.util.construct;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RailConstructTaskQueue {
    private final ConcurrentLinkedQueue<RailConstructTask> queue = new ConcurrentLinkedQueue<>();

    public RailConstructTaskQueue() {
    }

    public void add(RailConstructTask task) {
        task.processed = false;
        this.queue.add(task);
    }

    public RailConstructTask poll() {
        return this.queue.poll();
    }

}
