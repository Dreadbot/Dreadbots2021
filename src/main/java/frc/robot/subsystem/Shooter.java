package frc.robot.subsystem;

import com.revrobotics.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.utility.DreadbotMath;

public class Shooter {
	// Limit Switch Variables
	int minHoodPosition;
	int maxHoodPosition;
	boolean readyToAim = false;
	boolean lowerLimitHit = false;
	boolean upperLimitHit = false;

	// Motor Controllers
	private final CANSparkMax shooterMotor;
	private final CANSparkMax aimingMotor;

	// PID Controllers
	private final CANPIDController shooterMotorPID;
	private final CANPIDController aimingMotorPID;

	// Encoders
	private final CANEncoder shooterMotorEncoder;
	private final CANEncoder aimingMotorEncoder;

	/* These are static because once a object is created for a given channel, another object cannot be created
	 * with the same channel.
	 */
	// Limit Switches
	private static final DigitalInput upperLimitSwitch = new DigitalInput(1);
	private static final DigitalInput lowerLimitSwitch = new DigitalInput(2);
	// Vision Light
	private static final Solenoid visionLEDRing = new Solenoid(7);

	// Shooting Mechanism Variables
	private final double speed;
	private double aimPosition;
	private double range;

	public Shooter() {
		this(new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless),
			new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless));
	}

	public Shooter(CANSparkMax shooterMotor, CANSparkMax aimingMotor) {
		// Instantiate Motor Controllers
		this.shooterMotor = shooterMotor;
		this.aimingMotor = aimingMotor;

		// Get the PID Controller Objects
		shooterMotorPID = shooterMotor.getPIDController();
		aimingMotorPID = aimingMotor.getPIDController();

		// Get the Encoder Objects
		shooterMotorEncoder = shooterMotor.getEncoder();
		aimingMotorEncoder = aimingMotor.getEncoder();

		// Configure PID Controllers (values are tuned)
		shooterMotorPID.setP(9e-3);
		shooterMotorPID.setI(5e-7);
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

	public void aimHeight(double rotations) {
		aimingMotorPID.setReference(rotations, ControlType.kPosition);
	}

	public void setAimHeightP(double p) {
		aimingMotorPID.setP(p);
	}

	public void setShootingPercentOutput(double percentOutput) {
		shooterMotor.set(percentOutput);
	}

	public void setHoodPercentOutput(double percentOutput) {
		if (!lowerLimitSwitch.get() || !upperLimitSwitch.get())
			percentOutput = 0.0;

		aimingMotor.set(percentOutput);
	}

	public int getShootingSpeed() {
		// Cast to int for ease of comparison
		return (int) shooterMotorEncoder.getVelocity();
	}

	public void setLowerLimit(int position) {
		minHoodPosition = position;
		lowerLimitHit = true;
	}

	public void setUpperLimit(int position) {
		maxHoodPosition = position;
		upperLimitHit = true;
	}

	public void setUpperBool(boolean value) {
		upperLimitHit = value;
	}

	public void setLowerBool(boolean value) {
		lowerLimitHit = value;
	}

	boolean getAimReadiness() {
		return readyToAim;
	}

	public void setAimReadiness(boolean ready) {
		readyToAim = ready;
		if (ready)
			range = maxHoodPosition - minHoodPosition;
	}

	public int getHoodPosition() {
		return (int) aimingMotorEncoder.getPosition();
	}

	public void setHoodPosition(double position) {
		DreadbotMath.clampValue(position, 0.0d, 1.0d);

		position = minHoodPosition + (position * range);

		aimingMotorPID.setReference(position, ControlType.kPosition);
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

	public void setPID(double p, double i, double d) {
		shooterMotorPID.setP(p);
		shooterMotorPID.setI(i);
		shooterMotorPID.setD(d);
	}

	void setVisionLEDRingEnabled(boolean enabled) {
		visionLEDRing.set(enabled);
	}
}
