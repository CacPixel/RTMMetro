package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.util.ModLog;
import net.cacpixel.rtmmetro.util.RTMMetroException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiTheme
{
    public static final List<String> RESOURCE_NAMES = new ArrayList<>();
    public static GuiTheme defaultTheme;
    public static GuiTheme minecraftTheme;

    public static final Map<String, GuiTheme> THEME_MAP = new HashMap<>();
    public String name;
    public Map<String, ResourceLocation> locationMap = new HashMap<>();

    static
    {
        RESOURCE_NAMES.addAll(ResourceConstants.RESOURCE_LIST);
        onResourcePackReload();
    }

    public static void init()
    {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft()
                .getResourceManager();
        resourceManager.registerReloadListener(
                (ISelectiveResourceReloadListener) (resourceManager1, resourcePredicate) -> {
                    onResourcePackReload();
                });
    }

    public static void onResourcePackReload()
    {
        THEME_MAP.clear();
        defaultTheme = registerGuiTheme("default", RTMMetro.MODID);
        minecraftTheme = registerGuiTheme("minecraft", RTMMetro.MODID);
    }

    public GuiTheme(String name, String modID)
    {
        this.name = name;
        RESOURCE_NAMES.forEach(x -> {
            ResourceLocation[] locations = new ResourceLocation[]{
                    new ResourceLocation(modID, "textures/gui/theme/" + this.name + "/" + x + ".png"),
                    new ResourceLocation(modID, "textures/gui/theme/default/" + x + ".png"),
                    new ResourceLocation(modID, "textures/gui/" + x + ".png"),
            };
            for (ResourceLocation res : locations)
            {
                try
                {
                    Minecraft.getMinecraft().getResourceManager().getResource(res);
                    this.locationMap.put(x, res);
                    return;
                }
                catch (IOException e)
                {
                    ModLog.debug("Could not found file: " + res);
                }
            }
            throw new RTMMetroException("Could not found: " + x + ".png");
        });
    }

    public GuiTheme register()
    {
        THEME_MAP.put(this.name, this);
        return this;
    }

    public static GuiTheme registerGuiTheme(String name, String modID)
    {
        return new GuiTheme(name, modID).register();
    }

    public static GuiTheme getCurrent()
    {
        return THEME_MAP.getOrDefault(ModConfig.guiTheme, defaultTheme);
    }

    public static ResourceLocation getCurrentResourceLocation(String str)
    {
        return getCurrent().locationMap.getOrDefault(str, null);
    }
}
