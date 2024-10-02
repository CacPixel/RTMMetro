package net.cacpixel.rtmmetro.math;

import jp.ngt.ngtlib.math.NGTMath;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class CacMath
{
    public static double getWrappedAngle(double angleD)
    {
        return MathHelper.wrapDegrees(angleD);
    }

    public static float getWrappedAngle(float angleD)
    {
        return MathHelper.wrapDegrees(angleD);
    }

    public static double getWrappedAngleAndReverse(double angleD)
    {
        return MathHelper.wrapDegrees(angleD + 180.0d);
    }

    public static float getWrappedAngleAndReverse(float angleD)
    {
        return MathHelper.wrapDegrees(angleD + 180.0f);
    }

    public static int getRPDirection(int blockX, int blockZ, double posX, double posZ, boolean isCornerOnly)
    {
        double xOffset = posX - blockX;
        double zOffset = posZ - blockZ;
        return getRPDirection(new double[]{0.5, 0.5}, new double[]{zOffset, xOffset}, isCornerOnly);
    }

    public static int getRPDirection(double[] start, double[] end, boolean isCornerOnly)
    {
        if (start == null || end == null || start.length < 2 || end.length < 2)
        {
            return -1;
        }
        if (isCornerOnly)
        {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f);
            return (byte) (Math.floor(angle / 90.0f) * 2 + 1) & 0x7;
        }
        else
        {
            float angle = (float) NGTMath.normalizeAngle(getAngleD(start, end) + 180.0f + 22.5f);
            return (byte) Math.floor(angle / 45.0f) & 0x7;
        }
    }

    public static boolean isPointAcceptable(double[] in, double threshold)
    {
        double[] decimal = new double[]{in[0] - Math.floor(in[0]), in[1] - Math.floor(in[1])};
        int[] result = new int[]{0, 0};
        // 0 不支持
        // 1 趋向于0
        // 2 趋向于0.5
        // 3 趋向于1
        for (int i = 0; i < 2; i++)
        {
            if (0.0 <= decimal[i] && decimal[i] <= threshold)
            {
                result[i] = 1;
            }
            else if (1.0 - threshold <= decimal[i] && decimal[i] <= 1.0)
            {
                result[i] = 3;
            }
            else if (0.5 - threshold <= decimal[i] && decimal[i] <= 0.5 + threshold)
            {
                result[i] = 2;
            }
        }
        if (result[0] == result[1] && result[0] == 2)
        {
            return false; // 排除处在方块中心的情况
        }
        return ((result[0] > 0) && (result[1] > 0));
    }

    public static boolean isPointAcceptableCorner(double[] in, double threshold)
    {
        double[] decimal = new double[]{in[0] - Math.floor(in[0]), in[1] - Math.floor(in[1])};
        int[] result = new int[]{0, 0};
        // 0 不支持
        // 1 趋向于0
        // 2 趋向于0.5
        // 3 趋向于1
        for (int i = 0; i < 2; i++)
        {
            if (0.0 <= decimal[i] && decimal[i] <= threshold)
            {
                result[i] = 1;
            }
            else if (1.0 - threshold <= decimal[i] && decimal[i] <= 1.0)
            {
                result[i] = 3;
            }
        }
        return ((result[0] > 0) && (result[1] > 0));
    }

    public static double[] normalizePoint(double[] in)
    {
        double[] decimal = new double[]{in[0] - Math.floor(in[0]), in[1] - Math.floor(in[1])};
        double[] ret = new double[2];
        for (int i = 0; i < 2; i++)
        {
            if (decimal[i] <= 0.25)
            {
                ret[i] = Math.floor(in[i]) + 0.0;
            }
            else if (0.75 <= decimal[i])
            {
                ret[i] = Math.floor(in[i]) + 1.0;
            }
            else // if (0.25 <= decimal[i] && decimal[i] <= 0.75)
            {
                ret[i] = Math.floor(in[i]) + 0.5;
            }
        }
        return ret;
    }

    private static double[] swapAndCopy(double[] par1)
    {
        return new double[]{par1[1], par1[0]};
    }

    public static double getLength(double[] start, double[] end)
    {
        if (Arrays.equals(start, end))
        {
            return 0.0f;
        }
        else
        {
            return Math.sqrt(Math.pow(start[0] - end[0], 2) + Math.pow(start[1] - end[1], 2));
        }
    }

    public static float getAngleD(double[] start, double[] end)
    {
        if (start[0] == end[0])
        {
            if (start[1] < end[1]) return 90.0f;
            else if (start[1] > end[1]) return -90.0f;
            else return 0.0f;
        }
        else
        {
            return (float) NGTMath.toDegrees(Math.atan2(end[1] - start[1], end[0] - start[0]));
        }
    }
}
