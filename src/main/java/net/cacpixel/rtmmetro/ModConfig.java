package net.cacpixel.rtmmetro;

public class ModConfig
{
    public static int markerDisplayDistance = 100;
    public static int railGeneratingDistance = 1024;
    public static int railGeneratingHeight = 256;
    public static boolean useFastMarkerSearchMethod = true;
    public static boolean useModernMarkerSortMethod = true;

    /* 0 (<= 0) - All threads, N = Math.max(N, Runtime.getRuntime().availableProcessors() * 2) - N threads */
    public static int threadsToConstructRails = 24;
    public static boolean showDebugMessages = true;
    public static double railSplitThreshold = 0.075;
    public static double railSplitThresholdStraight = 0.02;
    public static int railSplitMinimumLength = 10;
    public static float guiAnimationDuration = 0.25F;

}
