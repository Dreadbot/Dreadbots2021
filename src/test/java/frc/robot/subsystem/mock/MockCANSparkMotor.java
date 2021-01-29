package frc.robot.subsystem.mock;

import com.revrobotics.*;

import static org.mockito.Mockito.mock;

public class MockCANSparkMotor extends CANSparkMax {

    CANPIDController pidController = mock(CANPIDController.class);
    CANEncoder encoder = mock(CANEncoder.class);
    private double rpm;

    public MockCANSparkMotor() {
        super(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        pidController = new MockCANPIDController(this);
        encoder = new MockCANEncoder(this);
    }

    @Override
    public CANPIDController getPIDController() {
        return pidController;
    }

    @Override
    public CANEncoder getEncoder() {
        return encoder;
    }

    public void setSpeed(double rpm) {
        this.rpm = rpm;
    }

    public double getSpeed() {
        return rpm;
    }
}
