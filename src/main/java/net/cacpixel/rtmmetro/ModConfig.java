package net.cacpixel.rtmmetro;

public class ModConfig {
    public static int railGeneratingDistance = 1024;
    public static int railGeneratingHeight = 256;
    public static boolean useFastMarkerSearchMethod = true;
    public static boolean useModernMarkerSortMethod = true;

    /* 0 (<= 0) - All threads, N = Math.max(N, Runtime.getRuntime().availableProcessors() * 2) - N threads */
    public static int threadsToConstructRails = 24;
    public static boolean showDebugMessages = true;

}
