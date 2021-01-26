package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Ultrasonic;

public class Ultra {
    Ultrasonic ultra1;
    public Ultra(int pingChannel, int echoChannel){
        ultra1 = new Ultrasonic(pingChannel, echoChannel);
    }

    public static void automatic(){
        Ultrasonic.setAutomaticMode(true);
    }
    
    public double getRangeInches(){
        return ultra1.getRangeInches();
    }
}
