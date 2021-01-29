package frc.robot.subsystem.mock;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

public class MockCANEncoder extends CANEncoder {

    private MockCANSparkMotor motor;

    public MockCANEncoder(MockCANSparkMotor device) {
        super(device);
        motor = device;
    }

    public double getVelocity() {
        return motor.getSpeed();
    }

    public double getPosition() {
        // return getSpeed() because the mocks use same variable for speed and position
        return motor.getSpeed();
    }
}
