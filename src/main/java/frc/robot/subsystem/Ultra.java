package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Ultrasonic;

public class Ultra extends Subsystem {
    Ultrasonic ultra1;

    public Ultra(int pingChannel, int echoChannel){
        super("Ultrasonics");
        ultra1 = new Ultrasonic(pingChannel, echoChannel);
        addTest(()->{
            System.out.println(getRangeInches());
        }, "500 ms Ultrasonic Readout", 0.5d);
    }

    public static void automatic(){
        Ultrasonic.setAutomaticMode(true);
    }
    
    public double getRangeInches(){
        return ultra1.getRangeInches();
    }
}
