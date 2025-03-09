package net.cacpixel.rtmmetro.modelpack.init;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.util.NGTUtilClient;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.relauncher.Side;

public class FastModelPackConstructThread extends Thread
{
    private final Side threadSide;
    private final ModelPackLoadThread parent;
    private boolean loading;
    private int index;
    private boolean barStateChanged;

    public FastModelPackConstructThread(Side par1, ModelPackLoadThread par2) {
        super("RTM ModelPack Construct");
        this.threadSide = par1;
        this.parent = par2;
        this.loading = true;
    }

    public void run() {
        try {
            this.runThread();
        } catch (Throwable var3) {
            if (this.threadSide == Side.CLIENT) {
                CrashReport crashReport = CrashReport.makeCrashReport(var3, "Constructing RTM ModelPack");
                crashReport.makeCategory("Initialization");
                crashReport = NGTUtilClient.getMinecraft().addGraphicsAndWorldToCrashReport(crashReport);
                NGTUtilClient.getMinecraft().displayCrashReport(crashReport);
            } else {
                var3.printStackTrace();
            }
        }

    }

    private void runThread() throws InterruptedException {
        while(this.loading) {
            while(ModelPackManager.INSTANCE.unconstructSets.size() > this.index) {
                ResourceSet set = (ResourceSet)ModelPackManager.INSTANCE.unconstructSets.get(this.index);
                if (this.threadSide == Side.SERVER) {
                    set.constructOnServer();
                } else {
                    set.constructOnClient();
                    set.finishConstruct();
                }

                NGTLog.debug("Construct Model : %s (%d / %d)", new Object[]{set.getConfig().getName(), this.index + 1, ModelPackManager.INSTANCE.unconstructSets.size()});
                ++this.index;
                if (this.parent.loadFinished) {
                    if (!this.barStateChanged) {
                        this.parent.setBarValue(0, ProgressStateHolder.ProgressState.CONSTRUCTING_MODEL);
                        int size = ModelPackManager.INSTANCE.unconstructSets.size();
                        this.parent.setBarMaxValue(1, size, "");
                        this.barStateChanged = true;
                    }

                    this.parent.setBarValue(1, this.index, set.getConfig().getName());
                }
            }

            sleep(500L);
        }

    }

    public boolean setFinish() {
        if (ModelPackManager.INSTANCE.unconstructSets.size() == this.index) {
            ModelPackManager.INSTANCE.unconstructSets.clear();
            ModelPackManager.INSTANCE.clearCache();
            this.loading = false;
            return true;
        } else {
            return false;
        }
    }
}
