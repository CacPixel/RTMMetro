package net.cacpixel.rtmmetro.traincontrollerdemo;

import gnu.io.*;
import jp.ngt.ngtlib.io.NGTLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class UartHandle<T extends SerialPortEventListener> {
    public String portName;
    public int baudRate;
    public int dataBits;
    public int stopBits;
    public int parity;
    private final T eventListener;
    public CommPortIdentifier portId; // 串口通信管理类
    public Enumeration<?> portList; // 有效连接上的端口的枚举
    protected InputStream inputStream; // 从串口来的输入流
    public OutputStream outputStream;// 向串口输出的流
    public SerialPort serialPort; // 串口的引用
    public static final int BUFFERR_LEN = 128;

    public String encoding = "gbk";
    public byte[] rxBuffer = new byte[BUFFERR_LEN];
    public boolean isRxComplete = false;
    private boolean initialized = false;

    public UartHandle(T eventListener, String portName, int baudRate, int dataBits, int stopBits, int parity) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.eventListener = eventListener;
    }

    public void readyForNextReceive() {
        this.rxBuffer = new byte[BUFFERR_LEN];
        this.isRxComplete = false;
    }

    public int startComPort() {
        return this.startComPort(this.eventListener, this.portName, this.baudRate, this.dataBits, this.stopBits, this.parity);
    }

    private int startComPort(T eventListener, String portName, int baudRate, int dataBits, int stopBits, int parity) {
        // 通过串口通信管理类获得当前连接上的串口列表
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {

            // 获取相应串口对象
            portId = (CommPortIdentifier) portList.nextElement();

            NGTLog.debug("设备类型：--->" + portId.getPortType());
            NGTLog.debug("设备名称：---->" + portId.getName());
            // 判断端口类型是否为串口
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().contains(portName)) {
                    try {
                        // 打开串口名字为COM_4(名字任意),延迟为2毫秒
                        serialPort = (SerialPort) portId.open(portName, 2000);

                    } catch (PortInUseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 设置当前串口的输入输出流
                    try {
                        inputStream = serialPort.getInputStream();
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 给当前串口添加一个监听器
                    try {
                        serialPort.addEventListener(eventListener);
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 设置监听器生效，即：当有数据时通知
                    serialPort.notifyOnDataAvailable(true);

                    // 设置串口的一些读写参数
                    try {
                        // 比特率、数据位、停止位、奇偶校验位
                        serialPort.setSerialPortParams(baudRate,
                                dataBits, stopBits,
                                parity);
                    } catch (UnsupportedCommOperationException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    this.initialized = true;
                    return 1;
                }
            }
        }
        return 0;
    }

    public void send(String s) throws IOException{
        this.outputStream.write(s.getBytes(this.encoding), 0, s.getBytes(this.encoding).length);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
