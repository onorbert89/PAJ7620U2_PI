import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;

import java.io.IOException;

public class PAJ7620U2 {

    //i2c address
    final static byte PAJ7620U2_I2C_ADDRESS = (byte)0x73;
    //Register Bank select
    final static byte PAJ_BANK_SELECT = (byte)0xEF;	/*Bank0=0x00,Bank1=0x01*/
    //Register Bank 0
    final static byte PAJ_SUSPEND = (byte)0x03;	/*I2C suspend command (Write 0x01 to enter suspend state). I2C wake-up command is slave ID wake-up. Refer to topic ��I2C Bus Timing Characteristics and Protocol��*/
    final static byte PAJ_INT_FLAG1_MASK = (byte)0x41;	/*Gesture detection interrupt flag mask*/
    final static byte PAJ_INT_FLAG2_MASK = (byte)0x42;	/*Gesture/PS detection interrupt flag mask*/
    final static byte PAJ_INT_FLAG1 = (byte)0x43;	/*Gesture detection interrupt flag*/
    final static byte PAJ_INT_FLAG2 = (byte)0x44;	/*Gesture/PS detection interrupt flag*/
    final static byte PAJ_STATE = (byte)0x45;	/*State indicator for gesture detection (Only functional at gesture detection mode)*/
    final static byte PAJ_PS_HIGH_THRESHOLD = (byte)0x69;	/*PS hysteresis high threshold (Only functional at proximity detection mode)*/
    final static byte PAJ_PS_LOW_THRESHOLD = (byte)0x6A;	/*PS hysteresis low threshold (Only functional at proximity detection mode)*/
    final static byte PAJ_PS_APPROACH_STATE = (byte)0x6B;	/*PS approach state,  Approach = 1 , (8 bits PS data >= PS high threshold),  Not Approach = 0 , (8 bits PS data <= PS low threshold)(Only functional at proximity detection mode)*/
    final static byte PAJ_PS_DATA = (byte)0x6C;	/*PS 8 bit data(Only functional at gesture detection mode)*/
    final static byte PAJ_OBJ_BRIGHTNESS = (byte)0xB0;	/*Object Brightness (Max. 255)*/
    final static byte PAJ_OBJ_SIZE_L = (byte)0xB1;	/*Object Size(Low 8 bit)*/
    final static byte PAJ_OBJ_SIZE_H = (byte)0xB2;	/*Object Size(High 8 bit)*/

    //Register Bank 1
    final static byte PAJ_PS_GAIN = (byte)0x44;	/*PS gain setting (Only functional at proximity detection mode)*/
    final static byte PAJ_IDLE_S1_STEP_L = (byte)0x67;	/*IDLE S1 Step, for setting the S1, Response Factor(Low 8 bit)*/
    final static byte PAJ_IDLE_S1_STEP_H = (byte)0x68;	/*IDLE S1 Step, for setting the S1, Response Factor(High 8 bit)*/
    final static byte PAJ_IDLE_S2_STEP_L = (byte)0x69;	/*IDLE S2 Step, for setting the S2, Response Factor(Low 8 bit)*/
    final static byte PAJ_IDLE_S2_STEP_H = (byte)0x6A;	/*IDLE S2 Step, for setting the S2, Response Factor(High 8 bit)*/
    final static byte PAJ_OPTOS1_TIME_L = (byte)0x6B;	/*OPtoS1 Step, for setting the OPtoS1 time of operation state to standby 1 state(Low 8 bit)*/
    final static byte PAJ_OPTOS2_TIME_H = (byte)0x6C;	/*OPtoS1 Step, for setting the OPtoS1 time of operation state to standby 1 stateHigh 8 bit)*/
    final static byte PAJ_S1TOS2_TIME_L = (byte)0x6D;	/*S1toS2 Step, for setting the S1toS2 time of standby 1 state to standby 2 state(Low 8 bit)*/
    final static byte PAJ_S1TOS2_TIME_H = (byte)0x6E;	/*S1toS2 Step, for setting the S1toS2 time of standby 1 state to standby 2 stateHigh 8 bit)*/
    final static byte PAJ_EN = (byte)0x72;	/*Enable/Disable PAJ7620U2*/
    //Gesture detection interrupt flag
    final static byte PAJ_UP = (byte)0x01;
    final static byte PAJ_DOWN = (byte)0x02;
    final static byte PAJ_LEFT = (byte)0x04;
    final static byte PAJ_RIGHT = (byte)0x08;
    final static byte PAJ_FORWARD = (byte)0x10;
    final static byte PAJ_BACKWARD = (byte)0x20;
    final static byte PAJ_CLOCKWISE = (byte)0x40;
    final static byte PAJ_COUNT_CLOCKWISE = (byte)0x80;
    final static byte PAJ_WAVE = (byte)0x100;

    //Power up initialize array
    static byte Init_Register_Array[][] = {
            {(byte)0xEF,(byte)0x00},
            {(byte)0x37,(byte)0x07},
            {(byte)0x38,(byte)0x17},
            {(byte)0x39,(byte)0x06},
            {(byte)0x41,(byte)0x00},
            {(byte)0x42,(byte)0x00},
            {(byte)0x46,(byte)0x2D},
            {(byte)0x47,(byte)0x0F},
            {(byte)0x48,(byte)0x3C},
            {(byte)0x49,(byte)0x00},
            {(byte)0x4A,(byte)0x1E},
            {(byte)0x4C,(byte)0x20},
            {(byte)0x51,(byte)0x10},
            {(byte)0x5E,(byte)0x10},
            {(byte)0x60,(byte)0x27},
            {(byte)0x80,(byte)0x42},
            {(byte)0x81,(byte)0x44},
            {(byte)0x82,(byte)0x04},
            {(byte)0x8B,(byte)0x01},
            {(byte)0x90,(byte)0x06},
            {(byte)0x95,(byte)0x0A},
            {(byte)0x96,(byte)0x0C},
            {(byte)0x97,(byte)0x05},
            {(byte)0x9A,(byte)0x14},
            {(byte)0x9C,(byte)0x3F},
            {(byte)0xA5,(byte)0x19},
            {(byte)0xCC,(byte)0x19},
            {(byte)0xCD,(byte)0x0B},
            {(byte)0xCE,(byte)0x13},
            {(byte)0xCF,(byte)0x64},
            {(byte)0xD0,(byte)0x21},
            {(byte)0xEF,(byte)0x01},
            {(byte)0x02,(byte)0x0F},
            {(byte)0x03,(byte)0x10},
            {(byte)0x04,(byte)0x02},
            {(byte)0x25,(byte)0x01},
            {(byte)0x27,(byte)0x39},
            {(byte)0x28,(byte)0x7F},
            {(byte)0x29,(byte)0x08},
            {(byte)0x3E,(byte)0xFF},
            {(byte)0x5E,(byte)0x3D},
            {(byte)0x65,(byte)0x96},
            {(byte)0x67,(byte)0x97},
            {(byte)0x69,(byte)0xCD},
            {(byte)0x6A,(byte)0x01},
            {(byte)0x6D,(byte)0x2C},
            {(byte)0x6E,(byte)0x01},
            {(byte)0x72,(byte)0x01},
            {(byte)0x73,(byte)0x35},
            {(byte)0x74,(byte)0x00},
            {(byte)0x77,(byte)0x01},
    };
    //Approaches register initialization array
    static byte Init_PS_Array[][] = {
            {(byte)0xEF,(byte)0x00},
            {(byte)0x41,(byte)0x00},
            {(byte)0x42,(byte)0x00},
            {(byte)0x48,(byte)0x3C},
            {(byte)0x49,(byte)0x00},
            {(byte)0x51,(byte)0x13},
            {(byte)0x83,(byte)0x20},
            {(byte)0x84,(byte)0x20},
            {(byte)0x85,(byte)0x00},
            {(byte)0x86,(byte)0x10},
            {(byte)0x87,(byte)0x00},
            {(byte)0x88,(byte)0x05},
            {(byte)0x89,(byte)0x18},
            {(byte)0x8A,(byte)0x10},
            {(byte)0x9f,(byte)0xf8},
            {(byte)0x69,(byte)0x96},
            {(byte)0x6A,(byte)0x02},
            {(byte)0xEF,(byte)0x01},
            {(byte)0x01,(byte)0x1E},
            {(byte)0x02,(byte)0x0F},
            {(byte)0x03,(byte)0x10},
            {(byte)0x04,(byte)0x02},
            {(byte)0x41,(byte)0x50},
            {(byte)0x43,(byte)0x34},
            {(byte)0x65,(byte)0xCE},
            {(byte)0x66,(byte)0x0B},
            {(byte)0x67,(byte)0xCE},
            {(byte)0x68,(byte)0x0B},
            {(byte)0x69,(byte)0xE9},
            {(byte)0x6A,(byte)0x05},
            {(byte)0x6B,(byte)0x50},
            {(byte)0x6C,(byte)0xC3},
            {(byte)0x6D,(byte)0x50},
            {(byte)0x6E,(byte)0xC3},
            {(byte)0x74,(byte)0x05},
    };

    //Gesture register initializes array
    static byte Init_Gesture_Array[][] = {
            {(byte)0xEF,(byte)0x00},
            {(byte)0x41,(byte)0x00},
            {(byte)0x42,(byte)0x00},
            {(byte)0xEF,(byte)0x00},
            {(byte)0x48,(byte)0x3C},
            {(byte)0x49,(byte)0x00},
            {(byte)0x51,(byte)0x10},
            {(byte)0x83,(byte)0x20},
            {(byte)0x9F,(byte)0xF9},
            {(byte)0xEF,(byte)0x01},
            {(byte)0x01,(byte)0x1E},
            {(byte)0x02,(byte)0x0F},
            {(byte)0x03,(byte)0x10},
            {(byte)0x04,(byte)0x02},
            {(byte)0x41,(byte)0x40},
            {(byte)0x43,(byte)0x30},
            {(byte)0x65,(byte)0x96},
            {(byte)0x66,(byte)0x00},
            {(byte)0x67,(byte)0x97},
            {(byte)0x68,(byte)0x01},
            {(byte)0x69,(byte)0xCD},
            {(byte)0x6A,(byte)0x01},
            {(byte)0x6B,(byte)0xB0},
            {(byte)0x6C,(byte)0x04},
            {(byte)0x6D,(byte)0x2C},
            {(byte)0x6E,(byte)0x01},
            {(byte)0x74,(byte)0x00},
            {(byte)0xEF,(byte)0x00},
            {(byte)0x41,(byte)0xFF},
            {(byte)0x42,(byte)0x01},
    };

    //Initialize array size
    final static int Init_Array = Init_Register_Array.length/2;
    final static int PS_Array_SIZE = Init_PS_Array.length/2;
    final static int Gesture_Array_SIZE = Init_Gesture_Array.length/2;

    I2CDevice device;

    public PAJ7620U2() {
    }

    public I2CDevice getDevice() throws IOException, I2CFactory.UnsupportedBusNumberException {
        if(this.device != null){
            return device;
        }

        // get the I2C bus to communicate on
        // - I2CBus.BUS_2 uses header pin CON6:3 as SDA and header pin CON6:5 as SCL
        // - I2CBus.BUS_3 uses header pin CON6:27 as SDA and header pin CON6:28 as SCL
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);

        // create an I2C device for an individual device on the bus that you want to communicate with
        // in this example we will use the default address for the TSL2561 chip which is 0x39.
        this.device = i2c.getDevice(PAJ7620U2_I2C_ADDRESS);

        return this.device;
    }

    //uint8_t
    byte I2C_readByte(byte addr) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CDevice device = getDevice();
        device.write(addr);
        return (byte) (device.read() & 0xFF);
    }

    boolean PAJ7620U2_init() throws IOException, I2CFactory.UnsupportedBusNumberException {
        byte State = I2C_readByte((byte) 0x00);                       //Read State
        if (State != 0x20){
            return false;                      //Wake up failed
        }
        I2C_writeByte(PAJ_BANK_SELECT, (byte) 0);                    //Select Bank 0
        for (byte i = 0; i< Init_Array; i++)
        {
            I2C_writeByte(Init_Register_Array[i][0], Init_Register_Array[i][1]);//Power up initialize
        }
        return true;
    }

    void I2C_writeByte(byte add, byte data) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CDevice device = getDevice();
        device.write(add, data);
    }

    //uint16_t
    byte I2C_readU16(byte addr) {
        return I2C_readU16(addr);
    }
}
