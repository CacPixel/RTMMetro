package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.ModConfig;
import net.cacpixel.rtmmetro.RTMMetro;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class GuiTheme
{
    public static final GuiTheme DEFAULT;
    public static final GuiTheme MINECRAFT;
    public static Map<String, GuiTheme> themeMap;
    public String name;
    public Map<String, ResourceLocation> locationMap = new HashMap<>();

    static
    {
        themeMap = new HashMap<>();
        DEFAULT = registerGuiTheme("default", RTMMetro.MODID);
        MINECRAFT = registerGuiTheme("minecraft", RTMMetro.MODID);
    }

    public GuiTheme(String name, String modID)
    {
        this.name = name;
        Stream.of("widgets").forEach(x -> this.locationMap.put(x,
                new ResourceLocation(modID, "textures/gui/theme/" + this.name + "/" + x + ".png")));
    }

    public GuiTheme register()
    {
        themeMap.put(this.name, this);
        return this;
    }

    public static GuiTheme registerGuiTheme(String name, String modID)
    {
        return new GuiTheme(name, modID).register();
    }

    public static GuiTheme getCurrent()
    {
        return themeMap.getOrDefault(ModConfig.guiTheme, DEFAULT);
    }

    public static ResourceLocation getCurrentResourceLocation(String str)
    {
        return getCurrent().locationMap.getOrDefault(str, null);
    }
}
