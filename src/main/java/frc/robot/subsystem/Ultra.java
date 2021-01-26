package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Ultrasonic;

public class Ultra {

    public Ultra(int pingChannel, int echoChannel){
        Ultrasonic ultra1 = new Ultrasonic(pingChannel, echoChannel);
    }

    public static void automatic(){
        Ultrasonic.setAutomaticMode(true);
    }
    
    public double getRangeInches(){
        return getRangeInches();
    }
}
