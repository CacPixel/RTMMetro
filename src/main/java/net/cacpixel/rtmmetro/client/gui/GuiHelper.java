package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.io.ScriptUtil;
import jp.ngt.ngtlib.math.NGTMath;
import net.cacpixel.rtmmetro.math.CacMath;
import net.cacpixel.rtmmetro.util.ModLog;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.MathHelper;

import javax.script.ScriptEngine;
import java.util.stream.Stream;

public class GuiHelper
{
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(GuiTextField field, T defaultVal)
    {
        T ret;
        String text = field.getText();
        String prefix = "importClass(Packages." + Math.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + NGTMath.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + CacMath.class.getCanonicalName() + "); \r\n" +
                "importClass(Packages." + MathHelper.class.getCanonicalName() + "); \r\n";
        try
        {
            // 禁用词：换行符 分号 字符串 importClass importPackage load class ClassLoader invoke null exec System java
            // test str: NGTMath.class.getClassLoader().loadClass("java.lang.Runtime").getMethod("getRuntime").invoke(null).exec("calc");
            if (Stream.of("\r", "\n", ";", "\"", "import", "class", "package", "load", "invoke", "null", "exec", "system", "java")
                    .anyMatch(text.toLowerCase()::contains))
            {
                ModLog.debug("Execution not allowed: " + text);
                return defaultVal;
            }
            ScriptEngine se = ScriptUtil.doScript(prefix + "x = " + text);
            String result = ScriptUtil.getScriptField(se, "x").toString();
//            ModLog.debug("Executing script: " + "x = " + text + "; Result is: " + result);
            if (defaultVal instanceof Byte)
            {
                ret = (T) Byte.valueOf(result);
            }
            else if (defaultVal instanceof Integer)
            {
                ret = (T) Integer.valueOf(result);
            }
            else if (defaultVal instanceof Float)
            {
                ret = (T) Float.valueOf(result);
            }
            else if (defaultVal instanceof Double)
            {
                ret = (T) Double.valueOf(result);
            }
            else
            {
                ModLog.debug("GuiHelper.getFieldValue : Type not supported : %s", defaultVal.getClass().toString());
                return defaultVal;
            }
        }
        catch (Throwable e)
        {
//            ModLog.debug("Expression syntax error: " + ((e.getCause() == null) ? e.getMessage() : e.getCause().getMessage()));
            return defaultVal;
        }
        return ret;
    }
}
