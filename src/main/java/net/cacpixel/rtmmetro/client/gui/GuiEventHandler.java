package net.cacpixel.rtmmetro.client.gui;

import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.client.gui.toast.GuiToastAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = RTMMetro.MODID)
public class GuiEventHandler
{
    @SubscribeEvent
    public static void onRenderTickEventEnd(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            GuiToastAdvanced.INSTANCE.drawToast(new ScaledResolution(GuiToastAdvanced.INSTANCE.getMinecraft()));
        }
    }

    @SubscribeEvent
    public static void onRenderTickEventStart(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiScreenAdvanced)
            {
                ((GuiScreenAdvanced) screen).handleInput();
            }
        }
    }
}
