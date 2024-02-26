package net.cacpixel.rtmmetro.traincontrollerdemo;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import jp.ngt.ngtlib.io.NGTLog;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import java.io.IOException;

public class NotchController extends Thread implements SerialPortEventListener, IUartDevice {

    public UartHandle<NotchController> huart;
    public byte lastReceivedByte = (byte) 0xff;
    public byte prevReceiveByte = lastReceivedByte;
    public int prevNotchValue = -8;
    private boolean loop;


    public NotchController() {
        this("COM17", 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    public NotchController(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        super("RTMMetro Train Controller Thread");
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
            this.start();
        }
    }

    public void deInit() {
        if (this.huart.isInitialized()) {
            this.loop = false;
            this.huart.serialPort.close();
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
                    while (huart.inputStream.available() > 0) {
                        int numBytes = huart.inputStream.read(huart.rxBuffer);
                        if (numBytes > 0) {
                            lastReceivedByte = this.huart.rxBuffer[0];
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    this.deInit();
                }
                break;
        }
    }

    @Override
    public void run() {
        try {
//            NGTLog.debug("Start uart receive content processing.");
            Thread.sleep(500);
            while (loop) {
                if (prevReceiveByte != lastReceivedByte) {
                    prevReceiveByte = lastReceivedByte;
                    int notchValue = prevNotchValue;
                    switch (lastReceivedByte) {
                        case (byte) 0x11:
                            notchValue = 5;
                            break;
                        case (byte) 0x22:
                            notchValue = 4;
                            break;
                        case (byte) 0x33:
                            notchValue = 3;
                            break;
                        case (byte) 0x44:
                            notchValue = 2;
                            break;
                        case (byte) 0x55:
                            notchValue = 1;
                            break;
                        case (byte) 0x66:
                            notchValue = 0;
                            break;
                        case (byte) 0x77:
                            notchValue = -1;
                            break;
                        case (byte) 0x88:
                            notchValue = -2;
                            break;
                        case (byte) 0x99:
                            notchValue = -3;
                            break;
                        case (byte) 0xaa:
                            notchValue = -4;
                            break;
                        case (byte) 0xbb:
                            notchValue = -5;
                            break;
                        case (byte) 0xcc:
                            notchValue = -6;
                            break;
                        case (byte) 0xdd:
                            notchValue = -7;
                            break;
                        case (byte) 0xee:
                        case (byte) 0xff:
                            notchValue = -8;
                            break;
                        default:
                            break;
                    }
                    this.setNotch(notchValue);
                    this.huart.readyForNextReceive();
                }
                Thread.sleep(50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        NGTLog.debug("Stop uart receive content processing.");
    }

    private void setNotch(int notch) {
        try {
            Entity entity = Minecraft.getMinecraft().player.getRidingEntity();
            if (entity instanceof EntityTrainBase) {
                if (((EntityTrainBase) entity).isControlCar()) {
                    ((EntityTrainBase) entity).setNotch(notch);
//                    NGTLog.showChatMessage("Rx byte from mcu: 0x%X, Notch: %d", this.lastReceivedByte, notch);
                }
            } else if (entity instanceof EntityFloor) {
                if (((EntityFloor) entity).getVehicle() instanceof EntityTrainBase) {
                    EntityTrainBase vehicle = (EntityTrainBase) ((EntityFloor) entity).getVehicle();
                    vehicle.setNotch(notch);
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

}
