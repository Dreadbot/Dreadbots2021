package frc.robot.subsystem;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class SparkDrive {
    /**
     * Motor Type of CANSparkMax on RedFive.
     */
    public static final CANSparkMaxLowLevel.MotorType K_MOTORTYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    
    /**
     * Collection of motors of the drivetrain.
     * Their indexes are as follows:
     * 0 - frontLeftMotor
     * 1 - frontRightMotor
     * 2 - backLeftMotor
     * 3 - backRightMotor
     */
    private List<CANSparkMax> motors; 

    public SparkDrive() {
        this.motors = new ArrayList<>();
        for(int i = 0; i < 4; i++)
            this.motors.set(i, new CANSparkMax(i, K_MOTORTYPE));
        
        this.stop();
    }

    /**
     * Stops all motors of the drivetrain.
     */
    public void stop() {
        for(CANSparkMax motor : motors)
            motor.set(0.0d);
    }
}
