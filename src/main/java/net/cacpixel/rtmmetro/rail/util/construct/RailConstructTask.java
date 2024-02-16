package net.cacpixel.rtmmetro.rail.util.construct;

public abstract class RailConstructTask {
    protected boolean processed;
    protected boolean running;

    public RailConstructTask() {
        this.processed = true;
        this.running = false;
    }

    public abstract void runTask();

    public void complete() {
        this.processed = true;
    }

    public boolean hasProcessed() {
        return this.processed;
    }
}
