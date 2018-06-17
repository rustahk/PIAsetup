package backend.devices;

import backend.core.ServiceProcessor;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.*;
import java.util.LinkedList;
/*
This class is realize connection to a device byt COM port, data in & output
 */


public class Connection {
    //**Port parameters**
    private String portname;
    private int baudrate;
    private int databits;
    private int stopbits;
    private int parity;
    //****
    private boolean status;
    private SerialPort port;
    private PortReader portreader;
    private boolean responceString;
    private int messagesize;

    private int responcedelay; //COM port and devices are not infinity fast. This parameter give


    public Connection(String portname, int baudrate, int databits, int stopbits, int parity, int messagesize, boolean responceString, int responcedelay) {
        status = false;
        this.portname = portname;
        this.baudrate = baudrate;
        this.databits = databits;
        this.stopbits = stopbits;
        this.parity = parity;
        this.messagesize = messagesize;
        this.responceString = responceString;
        this.responcedelay = responcedelay;

    }

    public void connect() throws SerialPortException {

        port = new SerialPort(portname);
        port.openPort();
        port.setParams(baudrate, databits, stopbits, parity);
        port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

        if (responceString) portreader = new StringPortReader();
        else portreader = new BytePortReader();

        port.addEventListener(portreader, SerialPort.MASK_RXCHAR);
        status = true;
        ServiceProcessor.serviceMessage(portname + " connection: OK");

    }

    public void sendMessage(int[] message) throws SerialPortException {
        port.writeIntArray(message);
    }

    public void sendMessage(String message) throws SerialPortException {
        port.writeString(message + '\r');
    }

    public int[] getByteResponce() throws IOException, InterruptedException {
        Thread.sleep(responcedelay);//DEALY TO GET REPLY, NOT HARD VALUE
        int[] reply = new int[messagesize];
        {
            for (int i = 0; i < messagesize; i++) {
                reply[i] = portreader.read();
            }
        }
        return reply;
    }

    public String getStringResponce() throws IOException, InterruptedException {
        Thread.sleep(responcedelay);
        return portreader.readString();
    }

    public void disconnect() throws SerialPortException {
        if (status) {
            port.closePort();
            status = false;
            ServiceProcessor.serviceMessage(portname + " disconnected");
        }
    }

    private class BytePortReader extends PortReader {
        byte[] buffer;
        LinkedList<Byte> list = new LinkedList<Byte>();

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    buffer = port.readBytes(event.getEventValue());

                    for (byte i : buffer) {
                        list.addLast(i);
                    }
                } catch (SerialPortException e1) {
                    System.out.println(e1);
                }
            }
        }

        public int read() throws IOException {
            return list.poll();
        }

        public String readString() {
            return null;
        }

        public int getInputSize() {
            return list.size();
        }
    }

    private class StringPortReader extends PortReader {
        LinkedList<String> list = new LinkedList<String>();

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    list.addLast(port.readString(event.getEventValue()));
                } catch (SerialPortException e1) {
                    System.out.println(e1);
                }
            }
        }

        public int read() {
            return 0;
        }

        public String readString() {
            String message = "";
            while (list.size() > 0) {
                message += list.poll();
            }
            return message;
        }

        public int getInputSize() {
            return list.size();
        }
    }

    private abstract class PortReader extends InputStream implements SerialPortEventListener {
        public abstract String readString();
    }
}


