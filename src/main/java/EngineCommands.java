import java.nio.ByteBuffer;

/*
This class realize byte arrays to step motor commands
Every command has 9 bytes (see manual to PD-1160 TMCL)

There is no checking of the values for safety limits!

EXAMPLE: (rotate right, speed 500) ROR 500 = {0x01, 0x01, 0x00, 0x00,0x00,0x00, 0x01, 0xf4, 0xf7};
*/


public class EngineCommands {

    int module_address = 0x01;
    int motor_number = 0x00;

    public int[] rotateRight(int velocity) {
        int command_number = 0x01;
        return createCommand(command_number, 0, velocity);
    }

    public int[] rotateLeft(int velocity) {
        int command_number = 0x02;
        return createCommand(command_number, 0, velocity);
    }

    public int[] motorStop() {
        int command_number = 0x03;
        return createCommand(command_number, 0, 0);
    }

    public int[] moveTo(int position) {
        int command_number = 0x04;
        return createCommand(command_number, 0, position);
    } //move to absolute position

    public int[] getPosition() //get axis parameter position
    {
        int command_number = 0x06; //Number from the motor manual
        return createCommand(command_number, 1, 0);
    }
    public int[] setPosition(int position) //set axis parameter position
    {
        int command_number = 0x05; //Number from the motor manual
        return createCommand(command_number, 1, position);
    }

    private byte calcChecksum(int[] message) {
        int checksumReal = 0;
        message[8]=0; //set calcChecksum byte to zero
        for (int i : message) {
            checksumReal += i;
        }
        return ByteBuffer.allocate(4).putInt(checksumReal).array()[3];
    } //calc last (9) byte for each command

    private int[] addValue(int[] command, int value) {
        byte[] valueBytes = ByteBuffer.allocate(4).putInt(value).array();
        for (int i = 0; i < 4; i++) {
            command[i + 4] = valueBytes[i];
        }
        return command;
    } //add int (4 bytes) value to byte array

    public int getValue(int[] reply) //eject int (4 bytes) value from byte array
    {
        String value = "";
        boolean minus = false;
        int a;
        int b = 2147483647;
        int c;
        if(reply[4]<0)
        {
            reply[4]-=128;
            minus = true;
        }
        for (int i = 4; i < 8; i++) {
            value+= String.format("%02X", (byte) reply[i]);
        }
        if (!minus) return Integer.parseInt(value, 16);
        c=b-Integer.parseInt(value, 16);
        return (c+1)*-1;
    }

    private int[] commandTemplate() {
        int[] command = new int[9];
        command[0] = module_address;
        command[3] = motor_number;
        return command;
    } //standart command template

    private int[] createCommand(int command_number, int type_number, int value) {
        int[] command = commandTemplate();
        command[1] = command_number;
        command[2] = type_number;
        command = addValue(command, value);//Bytes 4-7 to HEX (see package structure in the motor manual)
        command[8] = calcChecksum(command);
        return command;
    } //construct command

    public boolean commandStatus(int[] command, int[] reply) //check reply  & return ture if it is correct
    {
        //checklist
        if(reply[8]!= calcChecksum(reply)) return false; //calcChecksum byte
        if(reply[2]!=100) return false; //status byte
        if(command[1]!=reply[3]) return false; //command number byte
        return true;
    }
    public int[] getSpeed() //get current speed of a motor
    {
        int command_number = 0x06; //Number from the motor manual
        return createCommand(command_number, 3, 0);
    }
}
