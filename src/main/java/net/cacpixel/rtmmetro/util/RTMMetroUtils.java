package net.cacpixel.rtmmetro.util;

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
}
