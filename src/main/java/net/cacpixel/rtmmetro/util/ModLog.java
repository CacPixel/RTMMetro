package net.cacpixel.rtmmetro.util;

import jp.ngt.ngtlib.util.NGTUtil;
import jp.ngt.ngtlib.util.NGTUtilClient;
import net.cacpixel.rtmmetro.ModConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLog
{
    private static final Logger logger = LogManager.getLogger("RTMMetro");

    public static void debug(Object o)
    {
        debug(String.valueOf(o), new Object());
    }

    public static void debug(String str)
    {
        debug(str, new Object());
    }

    public static void debug(String str, Object... par2)
    {
        log(Level.DEBUG, str, par2);
    }

    public static void info(String str)
    {
        info(str, new Object[0]);
    }

    public static void info(String str, Object... par2)
    {
        log(Level.INFO, str, par2);
    }

    public static void warn(String str)
    {
        warn(str, new Object[0]);
    }

    public static void warn(String str, Object... par2)
    {
        log(Level.WARN, str, par2);
    }

    public static void error(String str)
    {
        error(str, new Object[0]);
    }

    public static void error(String str, Object... par2)
    {
        log(Level.ERROR, str, par2);
    }

    public static void fatal(String str)
    {
        fatal(str, new Object[0]);
    }

    public static void fatal(String str, Object... par2)
    {
        log(Level.FATAL, str, par2);
    }

    public static void trace(String str)
    {
        trace(str, new Object[0]);
    }

    public static void trace(String str, Object... par2)
    {
        log(Level.TRACE, str, par2);
    }

    public static void log(Level level, String par1, Object... par2)
    {
        if ((level == Level.DEBUG))
        {
            if (!ModConfig.showDebugMessages)
            {
                return;
            }
            level = Level.INFO;
        }
        try
        {
            String message = par1;
            if (par2 != null && par2.length > 0)
            {
                message = String.format(par1, par2);
            }
            logger.log(level, message);
        }
        catch (Exception e)
        {
        }
    }

    public static void sendChatMessage(ICommandSender player, String message, Object... objects)
    {
        player.sendMessage(new TextComponentTranslation(message, objects));
    }

    public static void sendChatMessageToAll(String message, Object... objects)
    {
        if (NGTUtil.getServer() == null)
        {
            debug("[RTMMetro] Can't send message. This is client.");
        }
        else
        {
            NGTUtil.getServer().getPlayerList().sendMessage(new TextComponentTranslation(message, objects));
        }

    }

    @SideOnly(Side.CLIENT)
    public static void showChatMessage(ITextComponent component)
    {
        NGTUtilClient.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
    }

    @SideOnly(Side.CLIENT)
    public static void showChatMessage(String message, Object... objects)
    {
        showChatMessage(new TextComponentString(String.format(message, objects)));
    }
}
