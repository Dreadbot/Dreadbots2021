package frc.robot.subsystem;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import frc.robot.utility.DreadbotMath;

public class SparkDrive {
    public static final CANSparkMaxLowLevel.MotorType K_MOTORTYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

    /**
     * Collection of motors of the drivetrain.
     * Their indexes are as follows:
     * 1 - frontLeftMotor
     * 2 - frontRightMotor
     * 3 - backLeftMotor
     * 4 - backRightMotor
     */
    private List<CANSparkMax> motors; 

    public SparkDrive() {
        this.motors = new ArrayList<>();
        for(int i = 0; i < 4; i++)
            this.motors.add(new CANSparkMax(i + 1, K_MOTORTYPE));
        
        this.stop();
    }

    /**
     * Stops all motors of the drivetrain.
     */
    public void stop() {
        for(CANSparkMax motor : motors)
            motor.set(0.0d);
    }

    /**
     * An improved and more readable version of the Dreadbot's homemade tank
     * drive function.
     * 
     * @param forwardAxisFactor    The forward factor of the drivetrain control.
     * @param rotationAxisFactor   The rotational factor of the drivetrain control.
     * @param finalValueMultiplier The final multiplier of the result of the
     *                             function.
     * @param joystickDeadband     The applied joystick deadband.
     */
    public void tankDrive(double forwardAxisFactor, 
                          double rotationAxisFactor, 
                          final double finalValueMultiplier,
                          final double joystickDeadband) {
        double[] speedControllerOutputs = new double[4];

        // Clamp Values to Acceptable Ranges (between -1.0 and 1.0).
        forwardAxisFactor = DreadbotMath.clampValue(forwardAxisFactor, -1.0d, 1.0d);
        rotationAxisFactor = DreadbotMath.clampValue(rotationAxisFactor, -1.0d, 1.0d);
        
        // Apply an Optional Joystick Deadband
        forwardAxisFactor = DreadbotMath.applyDeadbandToValue(forwardAxisFactor, -joystickDeadband, joystickDeadband, 0.0d);
        rotationAxisFactor = DreadbotMath.applyDeadbandToValue(rotationAxisFactor, -joystickDeadband, joystickDeadband, 0.0d);
        
        // Essential Drive Math based on the two movement factors.
        double leftFinalSpeed = -forwardAxisFactor + rotationAxisFactor;
        double rightFinalSpeed = forwardAxisFactor + rotationAxisFactor;
        
        // Assign each motor value to the output side it's on.
        speedControllerOutputs[0] = leftFinalSpeed;
        speedControllerOutputs[1] = rightFinalSpeed;
        speedControllerOutputs[2] = leftFinalSpeed;
        speedControllerOutputs[3] = rightFinalSpeed;
        
        // Add the final multiplier to the values.
        for(int i = 0; i < speedControllerOutputs.length; i++)
            speedControllerOutputs[i] *= finalValueMultiplier;
        
        // Normalize the values to become between 1.0 and -1.0.
        speedControllerOutputs = DreadbotMath.normalizeValues(speedControllerOutputs);
        
        // Assign each value of the array to the motor output.
        for(int i = 0; i < speedControllerOutputs.length; i++)
            motors.get(i).set(speedControllerOutputs[i]);
    }
}
