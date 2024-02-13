package net.cacpixel.rtmmetro.traincontrollerdemo;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.ngtlib.math.NGTMath;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;

public class SpeedMeter extends Thread implements SerialPortEventListener, IUartDevice {
    public UartHandle<SpeedMeter> huart;
    private boolean loop;
    private float speedToSet;
    private int timRegisterCCR;
    private int timRegisterPSC;
    private int timRegisterARR;
    private boolean isLightOn;

    public SpeedMeter() {
        this("COM7", 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    public SpeedMeter(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        super("RTMMetro SpeedMeter Thread");
        this.loop = true;
        this.huart = new UartHandle<>(this, portName, baudRate, dataBits, stopBits, parity);
    }

    @Override
    public void init() {
        if (this.huart.startComPort() == 1) {
            NGTLog.debug("[%s, %d-8-%d-%c]串口已打开!", huart.portName, huart.baudRate, huart.stopBits,
                    (huart.parity == SerialPort.PARITY_NONE) ? ('N')
                            : (huart.parity == SerialPort.PARITY_EVEN) ? ('E')
                            : (huart.parity == SerialPort.PARITY_ODD) ? ('O') : '?');
            this.speedToSet = 0.0f;
            this.start();
        } else {
            NGTLog.debug("串口打开失败!");
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:// 当有可用数据时读取数据
                try {
                    if (this.huart.isRxComplete) {
                        break;
                    }
                    while (huart.inputStream.available() > 0) {
                        int numBytes = huart.inputStream.read(huart.rxBuffer);
                        NGTLog.debug("numBytes = %d", numBytes);
                        if (numBytes > 0) {
                            this.huart.isRxComplete = true;
                        }
                    }
                } catch (IOException e) {
                    this.deInit();
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void run() {
        try {
            this.huart.send("/set:" + 120 + ";");
            Thread.sleep(500);
            this.huart.send("/set:" + this.speedToSet + ";");
            Thread.sleep(500);
            while (loop) {
                this.updateMeterSpeed();
                this.huart.send("/set:" + this.speedToSet + ";");
                Thread.sleep(20);
                if (isLightOn) {
                    this.huart.send("/light_on;");
                } else {
                    this.huart.send("/light_off;");
                }

//                this.huart.readyForNextReceive();
                Thread.sleep(50);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            this.deInit();
        }

    }

    public void deInit() {
        if (this.huart.isInitialized()) {
            try {
                this.loop = false;
                Thread.sleep(200);
                this.huart.send("/set:0;");
                Thread.sleep(20);
                this.huart.send("/light_off;");
            } catch (IOException | InterruptedException ignored) {
                ;
            }
            this.huart.serialPort.close();
        }
    }

    private void updateMeterSpeed() {
        try {
            Entity entity = Minecraft.getMinecraft().player.getRidingEntity();
            if (entity instanceof EntityTrainBase) {
                //if (((EntityTrainBase) entity).isControlCar()) {
                EntityTrainBase vehicle = (EntityTrainBase) entity;
                this.speedToSet = Math.abs(vehicle.getSpeed() * 72.0f);
                this.isLightOn = this.isInTunnel(vehicle);
                //}
            } else if (entity instanceof EntityFloor) {
                if (((EntityFloor) entity).getVehicle() instanceof EntityTrainBase) {
                    EntityTrainBase vehicle = (EntityTrainBase) ((EntityFloor) entity).getVehicle();
                    this.speedToSet = vehicle.getSpeed() * 72.0f;
                    this.isLightOn = this.isInTunnel(vehicle);
                }
            } else {
                this.speedToSet = 0.0f;
                this.isLightOn = false;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private boolean isInTunnel(EntityTrainBase vehicle) {
        World world = vehicle.world;
        int x = NGTMath.floor(vehicle.posX);
        int y = NGTMath.floor(vehicle.posY);
        int z = NGTMath.floor(vehicle.posZ);
        return !world.canSeeSky(new BlockPos(x + 1, y, z + 1)) &&
                !world.canSeeSky(new BlockPos(x - 1, y, z + 1)) &&
                !world.canSeeSky(new BlockPos(x + 1, y, z - 1)) &&
                !world.canSeeSky(new BlockPos(x - 1, y, z - 1));
    }
}
