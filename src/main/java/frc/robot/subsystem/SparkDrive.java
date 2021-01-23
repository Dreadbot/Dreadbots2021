package frc.robot.subsystem;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import frc.robot.utility.DreadbotMath;

public class SparkDrive {
    public static final CANSparkMaxLowLevel.MotorType K_MOTORTYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    public static final double K_JOYSTICK_DEADBAND = 0.2d;

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

    public void tankDrive(double forwardAxis, double rotationAxis, final double finalValueMultiplier, final double joystickDeadband) {
        DreadbotMath.clampValue(forwardAxis, -1.0d, 1.0d);
        DreadbotMath.clampValue(forwardAxis, -1.0d, 1.0d);
        
        DreadbotMath.applyDeadbandToValue(forwardAxis, -K_JOYSTICK_DEADBAND, K_JOYSTICK_DEADBAND, 0.0d);
        DreadbotMath.applyDeadbandToValue(rotationAxis, -K_JOYSTICK_DEADBAND, K_JOYSTICK_DEADBAND, 0.0d);
    }
}
