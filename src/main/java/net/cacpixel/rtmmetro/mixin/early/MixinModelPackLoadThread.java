package net.cacpixel.rtmmetro.mixin.early;

import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.RTMCore;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.init.ModelPackConstructThread;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.init.ProgressStateHolder;
import jp.ngt.rtm.network.PacketModelPack;
import jp.ngt.rtm.network.PacketNotice;
import net.cacpixel.rtmmetro.modelpack.init.ModelPackManagerEx;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelPackLoadThread.class, remap = false)
public abstract class MixinModelPackLoadThread extends Thread
{
    @Shadow @Final private Side threadSide;

    @Shadow public abstract void finish();

    @Shadow public abstract void setBarMaxValue(int var1, int var2, String var3);

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    public void run(CallbackInfo ci)
    {
        if (ModelPackManagerEx.INSTANCE.isInitialized())
        {
            ci.cancel();
        }
        ModelPackManagerEx.INSTANCE.setState(ModelPackManagerEx.State.INITIALIZED);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void runThread() throws InterruptedException
    {
        ModelPackLoadThread this$ModelPackLoadThread = (ModelPackLoadThread) ((Object) this);
        if (this.threadSide == Side.CLIENT && RTMCore.useServerModelPack)
        {
            this.setBarMaxValue(0, 0, "Waiting for connecting to Server");
            this.setBarMaxValue(1, 0, "You can start game");

            while (RTMCore.proxy.getConnectionState() == 0)
            {
                RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice((byte) 0, "getModelPack"));
                sleep(500L);
            }

            while (!PacketModelPack.MP_WRITER.finish)
            {
                sleep(500L);
            }
        }

        this.setBarMaxValue(0, ProgressStateHolder.ProgressState.values().length, "");
        NGTLog.startTimer();
        ModelPackManager.INSTANCE.load(this$ModelPackLoadThread);
        ModelPackConstructThread thread2 = new ModelPackConstructThread(this.threadSide, this$ModelPackLoadThread);
        ModelPackManagerEx.INSTANCE.addUnconstructSetsToQueue();
        thread2.start();

        while (!thread2.setFinish())
        {
            sleep(500L);
        }

        this.finish();
        NGTLog.stopTimer("Model load time");
        ModelPackManagerEx.INSTANCE.setState(ModelPackManagerEx.State.CONSTRUCTED);
        if (ModelPackManagerEx.INSTANCE.callingThread != null)
            ModelPackManagerEx.INSTANCE.callingThread.interrupt();
    }
}
