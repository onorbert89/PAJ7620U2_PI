import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;

import java.io.IOException;

public class Main {

    static PAJ7620U2 sensor;
    static Console console;

    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException {
        byte gestureData;
        PAJ7620U2 sensor = getSensor();
        Console console = getConsole();

        if(setup()){
            while (true) {
                gestureData = sensor.I2C_readU16(PAJ7620U2.PAJ_INT_FLAG1);

                if (Byte.valueOf(gestureData) != null) {
                    switch (gestureData) {
                        case PAJ7620U2.PAJ_UP:
                            console.println("Up");
                            break;
                        case PAJ7620U2.PAJ_DOWN:
                            console.println("Down");
                            break;
                        case PAJ7620U2.PAJ_LEFT:
                            console.println("Left");
                            break;
                        case PAJ7620U2.PAJ_RIGHT:
                            console.println("Right");
                            break;
                        case PAJ7620U2.PAJ_FORWARD:
                            console.println("Forward");
                            break;
                        case PAJ7620U2.PAJ_BACKWARD:
                            console.println("Backward");
                            break;
                        case PAJ7620U2.PAJ_CLOCKWISE:
                            console.println("Clockwise");
                            break;
                        case PAJ7620U2.PAJ_COUNT_CLOCKWISE:
                            console.println("AntiClockwise");
                            break;
                        case PAJ7620U2.PAJ_WAVE:
                            console.println("Wave");
                            break;
                        default:
                            break;
                    }
                    gestureData = 0;
                }
            }
        }
    }

    private static PAJ7620U2 getSensor(){
        if(sensor != null){
            return sensor;
        }
        sensor = new PAJ7620U2();
        return sensor;
    }

    private static boolean setup() throws IOException, I2CFactory.UnsupportedBusNumberException {
        PAJ7620U2 sensor = getSensor();
        Console console = getConsole();
        if(!sensor.PAJ7620U2_init()){
            console.println("Gesture Sensor Error");
            return false;
        }
        else {
            try{
                sensor.I2C_writeByte(sensor.PAJ_BANK_SELECT, (byte) 0);//Select Bank 0
                for (int i = 0; i < PAJ7620U2.Gesture_Array_SIZE; i++)
                {
                    sensor.I2C_writeByte(PAJ7620U2.Init_Gesture_Array[i][0], PAJ7620U2.Init_Gesture_Array[i][1]);//Gesture register initializes
                }
                console.println("Gesture Sensor OK");
                return true;
            } catch (Exception e) {
                console.println("Gesture Sensor Error");
                return false;
            }
        }
    }

    private static Console getConsole() {
        if (console != null) {
            return console;
        }

        console = new Console();

        // print program title/header
        console.title("<-- The PAJ7620U2 Project -->");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        return console;
    }
}
