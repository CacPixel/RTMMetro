package net.cacpixel.rtmmetro.modelpack.init;

import jp.ngt.ngtlib.NGTCore;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.ResourceType;
import jp.ngt.rtm.modelpack.init.ModelPackLoadThread;
import jp.ngt.rtm.modelpack.modelset.ResourceSet;
import net.cacpixel.rtmmetro.RTMMetroResource;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ModelPackManagerEx
{
    public final static ModelPackManagerEx INSTANCE = new ModelPackManagerEx();
    public final AtomicInteger index = new AtomicInteger(0);
    public final ConcurrentLinkedQueue<ResourceSet<?>> unconstructSetsQueue = new ConcurrentLinkedQueue<>();
    public final AtomicInteger size = new AtomicInteger(0);
    public final AtomicReference<String> lastLoadedModelName = new AtomicReference<>("");
    private volatile State state = State.UNINITIALIZED;

    public ModelPackManagerEx()
    {
    }

    public void addUnconstructSetsToQueue()
    {
        if (!ModelPackManager.INSTANCE.unconstructSets.isEmpty())
        {
            this.unconstructSetsQueue.addAll(ModelPackManager.INSTANCE.unconstructSets.stream()
                    .map(resourceSet -> (ResourceSet<?>) resourceSet)
                    .collect(Collectors.toList()));
            this.size.getAndAdd(ModelPackManager.INSTANCE.unconstructSets.size());
            ModelPackManager.INSTANCE.unconstructSets.clear();
        }
    }

    public void construct(ResourceSet<?> set, Side threadSide)
    {
        int i;
        try
        {
            if (threadSide == Side.SERVER)
            {
                set.constructOnServer();
            }
            else
            {
                set.constructOnClient();
                set.finishConstruct();
            }
            this.lastLoadedModelName.set(set.getConfig().getName());
        }
        catch (Throwable e)
        {
            CrashReport report = new CrashReport("RTM Model Pack Construct fail: " + this.lastLoadedModelName, e);
            Minecraft.getMinecraft().displayCrashReport(report);
        }
        finally
        {
            i = this.index.incrementAndGet();
        }
        NGTLog.debug("Construct Model : %s (%d / %d)", set.getConfig().getName(), i,
                size.get());
    }

    public void loadRTMModelPack()
    {
        loadRTMModelPack(NGTCore.proxy.isServer() ? Side.SERVER : Side.CLIENT);
    }

    public void loadRTMModelPack(Side side)
    {
        RTMResource.init();
        RTMMetroResource.init();
        ModelPackLoadThread thread = new ModelPackLoadThread(side);
        thread.start();
    }

    public void reloadRTMModelPack()
    {
        setState(State.UNINITIALIZED);
        loadRTMModelPack();
        while (state != State.CONSTRUCTED)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
            }
        }
        NGTLog.debug("Model Pack Reload Complete.");
    }

    public boolean isInitialized()
    {
        return state != State.UNINITIALIZED;
    }

    public void setState(State state)
    {
        this.state = state;
        if (state == State.UNINITIALIZED)
            resetAllCache();
    }

    public void waitForComplete()
    {
        while (state != State.CONSTRUCTED)
        {
            try
            {
                FMLCommonHandler.instance().processWindowMessages();
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
        NGTLog.debug("Model Pack Load Complete.");
    }

    @SuppressWarnings("unchecked, rawtypes")
    public void resetAllCache()
    {
        try
        {
            ((Map) getModelPackManagerDeclaredField("typeMap")).clear();
            Map<ResourceType, Map<String, ResourceSet>> allModelSetMap =
                    ((Map) getModelPackManagerDeclaredField("allModelSetMap"));
            Map<ResourceType, Map<String, ResourceSet>> smpModelSetMap =
                    ((Map) getModelPackManagerDeclaredField("smpModelSetMap"));
            ((Map) getModelPackManagerDeclaredField("dummyMap")).clear();
            ((List) getModelPackManagerDeclaredField("unconstructSets")).clear();
            ((Map) getModelPackManagerDeclaredField("modelCache")).clear();
            Map<String, Map<String, ResourceLocation>> resourceCache =
                    ((Map) getModelPackManagerDeclaredField("resourceCache"));
            ((Map) getModelPackManagerDeclaredField("scriptCache")).clear();
            ((List) getModelPackManagerDeclaredField("fileCache")).clear();
            allModelSetMap.forEach((k, v) -> v.clear());
            allModelSetMap.clear();
            smpModelSetMap.forEach((k, v) -> v.clear());
            smpModelSetMap.clear();
            resourceCache.forEach((k, v) -> v.clear());
            resourceCache.clear();
            ModelPackManager.INSTANCE.modelLoaded = false;
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    private Object getModelPackManagerDeclaredField(String str) throws NoSuchFieldException, IllegalAccessException
    {
        Field field = ModelPackManager.class.getDeclaredField(str);
        field.setAccessible(true);
        return field.get(ModelPackManager.INSTANCE);
    }

    public enum State
    {
        UNINITIALIZED,
        INITIALIZED,
        CONSTRUCTED
    }
}
