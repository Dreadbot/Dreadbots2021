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

    public void tankDrive(double forwardAxisFactor, 
                          double rotationAxis, 
                          final double finalValueMultiplier, 
                          final double joystickDeadband) {
        double[] speedControllerOutputs = new double[4];

        forwardAxisFactor = DreadbotMath.clampValue(forwardAxisFactor, -1.0d, 1.0d);
        rotationAxis = DreadbotMath.clampValue(rotationAxis, -1.0d, 1.0d);
        
        forwardAxisFactor = DreadbotMath.applyDeadbandToValue(forwardAxisFactor, -joystickDeadband, joystickDeadband, 0.0d);
        rotationAxis = DreadbotMath.applyDeadbandToValue(rotationAxis, -joystickDeadband, joystickDeadband, 0.0d);

        double leftFinalSpeed = -forwardAxisFactor - rotationAxis;
        double rightFinalSpeed = forwardAxisFactor - rotationAxis;

        motors.get(0).set(leftFinalSpeed * finalValueMultiplier);
        motors.get(2).set(leftFinalSpeed * finalValueMultiplier);

        // speedControllerOutputs[1] = leftFinalSpeed;
        // speedControllerOutputs[2] = rightFinalSpeed;
        // speedControllerOutputs[3] = leftFinalSpeed;
        // speedControllerOutputs[4] = rightFinalSpeed;

        // for(int i = 0; i < speedControllerOutputs.length; i++)
        //     speedControllerOutputs[i] *= finalValueMultiplier;
        
        // speedControllerOutputs = DreadbotMath.normalizeValues(speedControllerOutputs);

        // for(int i = 0; i < speedControllerOutputs.length; i++)
        //     motors.get(i).set(speedControllerOutputs[i]);
    }
}
