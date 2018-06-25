package backend.devices;

import backend.core.ErrorProcessor;
import backend.core.ServiceProcessor;
import jssc.*;

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
    private SerialPort port;
    private PortReader portreader;
    private boolean responceString;
    private int messagesize;

    private static LinkedList<SerialPort> opened_ports = new LinkedList<SerialPort>();

    private int responcedelay; //COM port and devices are not infinity fast. This parameter give


    public Connection(String portname, int baudrate, int databits, int stopbits, int parity, int messagesize, boolean responceString, int responcedelay) {
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
        addNewPort(port);
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
    public void cleanInputBuffer() throws NullPointerException
    {
        try
        {
            portreader.cleanInput();
        }
        catch (NullPointerException e)
        {
            ErrorProcessor.standartError(this.portname + " reader wasn't open", e);
            throw e;
        }
    }

    public String getStringResponce() throws IOException, InterruptedException {
        Thread.sleep(responcedelay);
        return portreader.readString();
    }

    public String getPortname() {
        return portname;
    }

    public void disconnect() throws SerialPortException {
        if (isOpened()) {
            port.closePort();
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
                    ErrorProcessor.standartError(Connection.this.portname, e1);
                }
            }
        }

        public int read() throws IOException {
            return list.poll();
        }

        public String readString() {
            return null;
        }

        public void cleanInput()
        {
            list = new LinkedList<Byte>();
        }
    }

    private class StringPortReader extends PortReader {
        LinkedList<String> list = new LinkedList<String>();

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    list.addLast(port.readString(event.getEventValue()));
                } catch (SerialPortException e1) {
                    ErrorProcessor.standartError(Connection.this.portname, e1);
                }
            }
        }

        public void cleanInput()
        {
            list = new LinkedList<String>();
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

    }

    private class CharPortReader extends PortReader {
        LinkedList<Character> list = new LinkedList<Character>();

        @Override
        public String readString() {
            String msg = "";
            while(true)
            {
                while(list.size() == 0)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        ErrorProcessor.standartError("Reading interrupted", e);
                        return null;
                    }
                }
                char i = list.poll();
                msg+=i;
                if(i == '\r') break;
            }
            return msg;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    for (char i : port.readString(event.getEventValue()).toCharArray()) {
                        list.addLast(i);
                    }
                } catch (SerialPortException e1) {
                    ErrorProcessor.standartError(Connection.this.portname, e1);
                }
            }
        }

        @Override
        public void cleanInput() {
            list = new LinkedList<Character>();
        }

        @Override
        public int read() {
            return 0;
        }
    }

    private abstract class PortReader extends InputStream implements SerialPortEventListener {
        public abstract String readString();
        public abstract void serialEvent(SerialPortEvent event);
        public abstract void cleanInput();

    }

    public static String[] getPortNames()
    {
        return SerialPortList.getPortNames();
    }

    public boolean isOpened()
    {
        return port.isOpened();
    }

    private static void addNewPort(SerialPort p)
    {
        opened_ports.add(p);
    }

    public static void cleanPorts()
    {
        for(SerialPort i : opened_ports)
        {
            if(i!=null)
            {
                try
                {
                    i.removeEventListener();;
                }
                catch (SerialPortException e)
                {
                    ErrorProcessor.standartError("Fail to remove port listener", e);
                }
                if(i.isOpened())
                {
                    try {
                        i.closePort();
                    }
                    catch (SerialPortException e)
                    {
                        ErrorProcessor.standartError("Fail to close port", e);
                    }
                }
            }
        }
    }
}


