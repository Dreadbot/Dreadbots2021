package frc.robot.subsystem;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import frc.robot.utility.Constants;
import frc.robot.utility.DreadbotMath;

import java.util.ArrayList;
import java.util.List;

public class SparkDrive extends Subsystem {
	public static final CANSparkMaxLowLevel.MotorType K_MOTORTYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

	// Feedforward gains
	public static final double kSVolts = 0.128d;
	public static final double kVVoltSecondsPerMeter = 0.0454d;
	public static final double kAVoltSecondsSquaredPerMeter = 0.0119d;
	public static final double kPDriveVel = 0.563d;

	public static final double kMaxSpeedMetersPerSecond = 0.5d;
	public static final double kMaxAccelerationMetersPerSecondSquared = 0.25d;

	public static final double kTrackwidthMeters = 0.6731d;
	public static final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(kTrackwidthMeters);
	private final List<CANSparkMax> motors;
	private final AHRS gyroscope;
	private final DifferentialDriveOdometry odometry;


	public SparkDrive() {
		super("SparkDrive");
		this.motors = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			CANSparkMax sparkMax = new CANSparkMax(i + 1, K_MOTORTYPE);
			sparkMax.restoreFactoryDefaults();

			// Setup PIDs
			final CANPIDController pidController = sparkMax.getPIDController();
			pidController.setP(0.2);
			pidController.setI(1e-4);
			pidController.setD(1);
			pidController.setIZone(0.1);
			pidController.setFF(0);
			pidController.setOutputRange(-1, 1);

			this.motors.add(sparkMax);
		}
		this.gyroscope = new AHRS(SerialPort.Port.kUSB);
		this.gyroscope.reset();

		this.odometry = new DifferentialDriveOdometry(gyroscope.getRotation2d());

		this.stop();

		configureTests();
	}

	private void configureTests() {
		// Runs a forward drive test for 2.5s, then stops the drivetrain.
		addTest(() -> tankDrive(1.0d, 0.0d, DriveMode.TURTLE, 0.0d), "Forward Drive Test", 2.5d);
		addTest(this::stop, 1.0d);

		// Runs a backward drive test for 2.5s, then stops the drivetrain.
		addTest(() -> tankDrive(-1.0d, 0.0d, DriveMode.TURTLE, 0.0d), "Backward Drive Test", 2.5d);
		addTest(this::stop, 1.0d);

		// Runs a rotation (positive) drive test for 2.5s, then stops the drivetrain.
		addTest(() -> tankDrive(0.0d, 1.0d, DriveMode.TURTLE, 0.0d), "Positive Rotation Drive Test", 2.5d);
		addTest(this::stop, 1.0d);

		// Runs a rotation (negative) drive test for 2.5s, then stops the drivetrain.
		addTest(() -> tankDrive(0.0d, -1.0d, DriveMode.TURTLE, 0.0d), "Negative Rotation Drive Test", 2.5d);
		addTest(this::stop, 1.0d);
	}

	public void periodic() {
		odometry.update(gyroscope.getRotation2d(),
			getMotorEncoder(1).getPosition() * Constants.revolutionsToMeters,
			getMotorEncoder(2).getPosition() * Constants.revolutionsToMeters);
	}

	public Pose2d getPose() {
		return odometry.getPoseMeters();
	}

	public DifferentialDriveWheelSpeeds getWheelSpeeds() {
		return new DifferentialDriveWheelSpeeds(
			getMotorEncoder(1).getVelocity() * Constants.revolutionsPerMinuteToMetersPerSecond,
			getMotorEncoder(2).getVelocity() * Constants.revolutionsPerMinuteToMetersPerSecond);
	}

	public void resetOdometry(Pose2d pose) {
		resetEncoders();
		odometry.resetPosition(pose, gyroscope.getRotation2d());
	}

	public void tankDriveVolts(double leftVolts, double rightVolts) {
		motors.get(0).setVoltage(leftVolts);
		motors.get(2).setVoltage(leftVolts);
		motors.get(1).setVoltage(-rightVolts);
		motors.get(3).setVoltage(-rightVolts);
	}

	public void resetEncoders() {
		getMotorEncoder(1).setPosition(0.0);
		getMotorEncoder(2).setPosition(0.0);
	}

	public double getAverageEncoderDistance() {
		return (getMotorEncoder(1).getPosition() + getMotorEncoder(2).getPosition()) / 2;
	}

	public void zeroHeading() {
		gyroscope.reset();
	}

	public double getHeading() {
		return gyroscope.getYaw();
	}

	public double getTurnRate() {
		return -gyroscope.getRate();
	}

	/**
	 * Stops all motors of the drivetrain.
	 */
	public void stop() {
		for (CANSparkMax motor : motors)
			motor.set(0.0d);
	}

	/**
	 * An improved and more readable version of the Dreadbot's homemade tank
	 * drive function with default values for final value multiplier and joystick
	 * deadband.
	 *
	 * @param forwardAxisFactor  The forward factor of the drivetrain control.
	 * @param rotationAxisFactor The rotational factor of the drivetrain control.
	 */
	public void tankDrive(double forwardAxisFactor,
	                      double rotationAxisFactor) {
		tankDrive(forwardAxisFactor, rotationAxisFactor, DriveMode.NORMAL);
	}

	/**
	 * An improved and more readable version of the Dreadbot's homemade tank
	 * drive function with default values for the joystick deadband.
	 *
	 * @param forwardAxisFactor  The forward factor of the drivetrain control.
	 * @param rotationAxisFactor The rotational factor of the drivetrain control.
	 * @param driveMode          The drive mode setting (final multiplier).
	 */
	public void tankDrive(double forwardAxisFactor,
	                      double rotationAxisFactor,
	                      final DriveMode driveMode) {
		tankDrive(forwardAxisFactor, rotationAxisFactor, driveMode, 0.09);
	}

	/**
	 * An improved and more readable version of the Dreadbot's homemade tank
	 * drive function.
	 *
	 * @param forwardAxisFactor  The forward factor of the drivetrain control.
	 * @param rotationAxisFactor The rotational factor of the drivetrain control.
	 * @param driveMode          The drive mode setting (final multiplier).
	 * @param joystickDeadband   The applied joystick deadband.
	 */
	public void tankDrive(double forwardAxisFactor,
	                      double rotationAxisFactor,
	                      final DriveMode driveMode,
	                      final double joystickDeadband) {
		double[] speedControllerOutputs = new double[4];

		// System.out.println("TankDrive: forward:" + forwardAxisFactor
		// 	+ " rotation:" + rotationAxisFactor + "  mode: " + driveMode
		// 	+ " deadband: " + joystickDeadband );
		// Clamp Values to Acceptable Ranges (between -1.0 and 1.0).
		forwardAxisFactor = DreadbotMath.clampValue(forwardAxisFactor, -1.0d, 1.0d);
		rotationAxisFactor = DreadbotMath.clampValue(rotationAxisFactor, -1.0d, 1.0d);
		//forwardAxisFactor *= Constants.DRIVE_SPEED_MULTIPLIER;
		//rotationAxisFactor *= Constants.DRIVE_SPEED_MULTIPLIER;

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
		for (int i = 0; i < speedControllerOutputs.length; i++)
			speedControllerOutputs[i] *= driveMode.finalValueMultiplier;

		// Normalize the values to become between 1.0 and -1.0.
		speedControllerOutputs = DreadbotMath.normalizeValues(speedControllerOutputs);

		// Assign each value of the array to the motor output.
		for (int i = 0; i < speedControllerOutputs.length; i++)
			motors.get(i).set(speedControllerOutputs[i]);
	}

	public AHRS getGyroscope() {
		return gyroscope;
	}

	public List<CANSparkMax> getMotors() {
		return motors;
	}

	public CANSparkMax getMotor(int port) {
		if (!DreadbotMath.inRange(port, 0, motors.size() + 1))
			return null;
		return motors.get(port - 1);
	}

	public CANEncoder getMotorEncoder(int port) {
		CANSparkMax motor = getMotor(port);
		if (motor == null)
			return null;

		return motor.getEncoder();
	}

	public CANPIDController getMotorPIDController(int port) {
		CANSparkMax motor = getMotor(port);
		if (motor == null)
			return null;

		return motor.getPIDController();
	}

	/**
	 * DriveMode is the enumeration of the default final value multipliers for teleop.
	 */
	public enum DriveMode {
		TURBO(0.9),
		NORMAL(0.5),
		TURTLE(0.2);

		public double finalValueMultiplier;

		DriveMode(double finalValueMultiplier) {
			this.finalValueMultiplier = finalValueMultiplier;
		}
	}
}
