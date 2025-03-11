package net.cacpixel.rtmmetro.util;

import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class RTMMetroUtils
{
    public static Class<?> getClassByPrimitiveType(Class<?> c)
    {
        if (!c.isPrimitive()) return c;
        switch (c.getName())
        {
        case "byte":
            c = Byte.class;
            break;
        case "short":
            c = Short.class;
            break;
        case "int":
            c = Integer.class;
            break;
        case "long":
            c = Long.class;
            break;
        case "float":
            c = Float.class;
            break;
        case "double":
            c = Double.class;
            break;
        case "boolean":
            c = Boolean.class;
            break;
        case "char":
            c = Character.class;
            break;
        default:
            break;
        }
        return c;
    }

    public static String getStackTrace(Throwable cause)
    {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Throwable throwable = cause;

        if (throwable.getMessage() == null)
        {
            if (throwable instanceof NullPointerException)
            {
                throwable = new NullPointerException();
            }
            else if (throwable instanceof StackOverflowError)
            {
                throwable = new StackOverflowError();
            }
            else if (throwable instanceof OutOfMemoryError)
            {
                throwable = new OutOfMemoryError();
            }

            throwable.setStackTrace(cause.getStackTrace());
        }

        String s = throwable.toString();

        try
        {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            throwable.printStackTrace(printwriter);
            s = stringwriter.toString();
        }
        finally
        {
            IOUtils.closeQuietly((Writer) stringwriter);
            IOUtils.closeQuietly((Writer) printwriter);
        }

        return s;
    }
}
