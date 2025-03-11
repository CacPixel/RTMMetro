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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Mixin(value = ModelPackConstructThread.class, remap = false)
public abstract class MixinModelPackConstructThread extends Thread
{
    @Final @Shadow private Side threadSide;
    @Final @Shadow private ModelPackLoadThread parent;
    @Shadow private boolean loading;
    @Shadow private boolean barStateChanged;
    @Unique private final static ModelPackManagerEx ManagerEx = ModelPackManagerEx.INSTANCE;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void runThread() throws InterruptedException, ExecutionException
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
                        int size = ManagerEx.size.get();
                        this.parent.setBarMaxValue(ProgressStateHolder.BAR_SUB, size, "");
                        this.barStateChanged = true;
                    }
                    this.parent.setBarValue(ProgressStateHolder.BAR_SUB, ManagerEx.index.get(),
                            ManagerEx.lastLoadedModelName.get());
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
            ManagerEx.addUnconstructSetsToQueue();
            while (ManagerEx.size.get() > ManagerEx.index.get())
            {
                ResourceSet<?> set = ManagerEx.unconstructSetsQueue.poll();
                if (set == null)
                {
                    break;
                }
                futures.add(exec.submit(() -> {
                    ManagerEx.construct(set, this.threadSide);
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
        if (ManagerEx.size.get() == ManagerEx.index.get())
        {
            ModelPackManager.INSTANCE.unconstructSets.clear();
            ModelPackManager.INSTANCE.clearCache();
            ManagerEx.index.set(0);
            ManagerEx.size.set(0);
            ManagerEx.lastLoadedModelName.set("");
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
