package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.init.ModelPackConstructThread;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = ModelPackConstructThread.class, remap = false)
public abstract class MixinModelPackConstructThread extends Thread
{
    @Final @Shadow private Side threadSide;
    @Final @Shadow private ModelPackLoadThread parent;
    @Shadow private boolean loading;
    @Unique private AtomicInteger rtmmetro$index = new AtomicInteger(0);
    @Shadow private boolean barStateChanged;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void runThread() throws InterruptedException
    {
        while (this.loading)
        {
            while (ModelPackManager.INSTANCE.unconstructSets.size() > this.rtmmetro$index.get())
            {
                ResourceSet set = (ResourceSet) ModelPackManager.INSTANCE.unconstructSets.get(this.rtmmetro$index.get());
                if (this.threadSide == Side.SERVER)
                {
                    set.constructOnServer();
                }
                else
                {
                    set.constructOnClient();
                    set.finishConstruct();
                }

                NGTLog.debug("Construct Model : %s (%d / %d)",
                        set.getConfig().getName(), this.rtmmetro$index.get() + 1, ModelPackManager.INSTANCE.unconstructSets.size());
                rtmmetro$index.getAndIncrement();
                if (this.parent.loadFinished)
                {
                    if (!this.barStateChanged)
                    {
                        this.parent.setBarValue(0, ProgressStateHolder.ProgressState.CONSTRUCTING_MODEL);
                        int size = ModelPackManager.INSTANCE.unconstructSets.size();
                        this.parent.setBarMaxValue(1, size, "");
                        this.barStateChanged = true;
                    }

                    this.parent.setBarValue(1, this.rtmmetro$index.get(), set.getConfig().getName());
                }
            }

            sleep(500L);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean setFinish() {
        if (ModelPackManager.INSTANCE.unconstructSets.size() == this.rtmmetro$index.get()) {
            ModelPackManager.INSTANCE.unconstructSets.clear();
            ModelPackManager.INSTANCE.clearCache();
            this.loading = false;
            return true;
        } else {
            return false;
        }
    }
}
