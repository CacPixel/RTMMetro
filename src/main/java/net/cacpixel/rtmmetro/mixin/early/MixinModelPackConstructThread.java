package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.init.ModelPackConstructThread;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import net.cacpixel.rtmmetro.modelpack.init.ModelPackManagerEx;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Mixin(value = ModelPackConstructThread.class, remap = false)
public abstract class MixinModelPackConstructThread extends Thread
{
    @Final @Shadow private Side threadSide;
    @Final @Shadow private ModelPackLoadThread parent;
    @Shadow private boolean loading;
    @Shadow private boolean barStateChanged;
    @Unique private final static ModelPackManagerEx FMPC = ModelPackManagerEx.INSTANCE;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void runThread() throws InterruptedException, ExecutionException
    {
        Thread guiUpdateThread = new Thread(() -> {
            while (this.loading)
            {
                if (this.parent.loadFinished)
                {
                    if (!this.barStateChanged)
                    {
                        this.parent.setBarValue(ProgressStateHolder.BAR_MAIN,
                                ProgressStateHolder.ProgressState.CONSTRUCTING_MODEL);
                        int size = FMPC.size.get();
                        this.parent.setBarMaxValue(ProgressStateHolder.BAR_SUB, size, "");
                        this.barStateChanged = true;
                    }
                    this.parent.setBarValue(ProgressStateHolder.BAR_SUB, FMPC.index.get(),
                            FMPC.lastLoadedModelName.get());
                }
                try
                {
                    sleep(50);
                }
                catch (InterruptedException e)
                {
                    ;
                }
            }
        });
        guiUpdateThread.start();

        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<?>> futures = new ArrayList<>();

        while (this.loading)
        {
            FMPC.addUnconstructSetsToQueue();
            while (FMPC.size.get() > FMPC.index.get())
            {
                ResourceSet<?> set = FMPC.unconstructSetsQueue.poll();
                if (set == null)
                {
                    break;
                }
                futures.add(exec.submit(() -> {
                    FMPC.construct(set, this.threadSide);
                }));
            }
            sleep(50L);
        }
        for (Future<?> future : futures)
        {
            future.get();
        }
        exec.shutdown();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean setFinish()
    {
        if (FMPC.size.get() == FMPC.index.get())
        {
            ModelPackManager.INSTANCE.unconstructSets.clear();
            ModelPackManager.INSTANCE.clearCache();
            FMPC.index.set(0);
            FMPC.size.set(0);
            FMPC.lastLoadedModelName.set("");
            this.loading = false;
            System.gc();
            return true;
        }
        else
        {
            return false;
        }
    }
}
