package net.cacpixel.rtmmetro.rail.util.construct;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RailConstructTask
{
    public AtomicBoolean completed = new AtomicBoolean(false);

    public RailConstructTask()
    {

    }

    public abstract void runTask();

    public void stopTask()
    {
        completed.getAndSet(true);
    }

}
