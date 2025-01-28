package net.cacpixel.rtmmetro.util;

import net.cacpixel.rtmmetro.ModConfig;
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
}
