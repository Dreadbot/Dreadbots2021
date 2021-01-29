package frc.robot.subsystem.mock;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

public class MockCANPIDController extends CANPIDController {

    private double referenceRPM = 0.0;
    private MockCANSparkMotor motor;

    public MockCANPIDController(MockCANSparkMotor device) {
        super(device);
        this.motor = device;
    }

    @Override
    public CANError setReference(double value, ControlType ctrl) {
        motor.setSpeed(value);
        return null;
    }

}
