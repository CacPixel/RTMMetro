package net.cacpixel.rtmmetro.traincontrollerdemo;

public class UartDevices
{

    public static NotchController notchController;
    public static SpeedMeter speedMeter;

    public static void initDevices() throws UnsatisfiedLinkError
    {
        notchController = new NotchController();
        speedMeter = new SpeedMeter();
        notchController.init();
        speedMeter.init();
    }

    public static void deInitDevices()
    {
        if (LibRxtxLoader.isLoaded())
        {
            notchController.deInit();
            speedMeter.deInit();
        }
    }

}
