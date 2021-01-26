package frc.robot.subsystem;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.utility.DreadbotMath;

public class Shooter {
    // Motor Controllers
    private CANSparkMax shooterMotor;
    private CANSparkMax aimingMotor;

    // PID Controllers
    private CANPIDController shooterMotorPID;
    private CANPIDController aimingMotorPID;

    // Encoders
    private CANEncoder shooterMotorEncoder;
    private CANEncoder aimingMotorEncoder;
    
    // Limit Switches
    private DigitalInput upperLimitSwitch;
    private DigitalInput lowerLimitSwitch;

    // Shooting Mechanism Variables
    private double speed;
    private double aimPosition;
    private double range;

    // Limit Switch Variables
    int minHoodPosition;
    int maxHoodPosition;
    boolean readyToAim = false;
    boolean lowerLimitHit = false;
    boolean upperLimitHit = false;

    public Shooter() {
        // Instantiate Motor Controllers
        shooterMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        aimingMotor = new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless);
        
        // Get the PID Controller Objects
        shooterMotorPID = shooterMotor.getPIDController();
        aimingMotorPID = aimingMotor.getPIDController();
        
        // Get the Encoder Objects
        shooterMotorEncoder = shooterMotor.getEncoder();
        aimingMotorEncoder = aimingMotor.getEncoder();
        
        // Instantiate Limit Switches
        upperLimitSwitch = new DigitalInput(1);
        lowerLimitSwitch = new DigitalInput(2);
        
        // Configure PID Controllers (values are tuned)
        shooterMotorPID.setP(6e-5);
        shooterMotorPID.setI(1e-5);
        shooterMotorPID.setD(0);
        shooterMotorPID.setIZone(0);
        shooterMotorPID.setFF(0.000015);
        shooterMotorPID.setOutputRange(-1.0, 1.0);
        speed = 0.0;
        
        aimingMotorPID.setP(0.1);
        aimingMotorPID.setI(0);
        aimingMotorPID.setD(0);
        aimingMotorPID.setIZone(0);
        aimingMotorPID.setFF(0.000015);
        aimingMotorPID.setOutputRange(-1.0, 1.0);
    }
    
    public void shoot(int rpm) {
        shooterMotorPID.setReference(rpm, ControlType.kVelocity);
    }
    
    public void aimHeight(double rotations){
        aimingMotorPID.setReference(rotations, ControlType.kPosition);
    }
    
    public void setAimHeightP(double p) {
        aimingMotorPID.setP(p);
    }
    
    public void setShootingPercentOutput(double percentOutput) {
        shooterMotor.set(percentOutput);
    }
    
    public void setHoodPercentOutput(double percentOutput) {
        
        if((!lowerLimitSwitch.get() && percentOutput > 0) {
            percentOutput = 0.0;
        }
        else if(!upperLimitSwitch.get() && percentOutput < 0){
            percentOutput = 0.0;
        }

        aimingMotor.set(percentOutput);
    }
    
    public int getShootingSpeed() {
        // Cast to int for ease of comparison
        return (int) shooterMotorEncoder.getVelocity();
    }

    public void setHoodPosition(double position) {
        DreadbotMath.clampValue(position, 0.0d, 1.0d);

        position = minHoodPosition + (position * range);
        
        aimingMotorPID.setReference(position, ControlType.kPosition);
    }

    public void setLowerLimit(int position) {
        minHoodPosition = position;
        lowerLimitHit = true;
    }
    
    public void setUpperLimit(int position) {
        maxHoodPosition = position;
        upperLimitHit = true;
    }

    public void setAimReadiness(boolean ready) {
        readyToAim = ready;
        if(ready)
            range = maxHoodPosition - minHoodPosition;
    }
    
    public void setUpperBool(boolean value) {
        upperLimitHit = value;
    }

    public void setLowerBool(boolean value) {
        lowerLimitHit = value;
    }

    public int getHoodPosition() {
        return (int) aimingMotorEncoder.getPosition();
    }

    public boolean getUpperLimitSwitch() {
        return !upperLimitSwitch.get();
    }

    public boolean getLowerLimitSwitch() {
        return !lowerLimitSwitch.get();
    }

    public boolean getUpperLimitBool() {
        return upperLimitHit;
    }
    
    public boolean getLowerLimitBool() {
        return lowerLimitHit;
    }

    public void setPID(double p, double i, double d){
        shooterMotorPID.setP(p);
        shooterMotorPID.setI(i);
        shooterMotorPID.setD(d);
    }
}
