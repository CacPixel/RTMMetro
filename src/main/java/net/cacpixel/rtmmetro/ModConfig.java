package net.cacpixel.rtmmetro;

public class ModConfig {
    public static int railGeneratingDistance = 1024;
    public static int railGeneratingHeight = 256;
    public static boolean useFastMarkerSearchMethod = true;
    public static boolean useModernMarkerSortMethod = true;

    /* 0 (<= 0) - All threads, N = Math.max(N, Runtime.getRuntime().availableProcessors()) - N threads */
    public static int threadsToConstructRails = 0;
    public static int multiThreadBezierCurveInitThreshold = 8192;
    public static boolean showDebugMessages = false;

}
