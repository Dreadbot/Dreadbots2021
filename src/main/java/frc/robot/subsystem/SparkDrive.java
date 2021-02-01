package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.robot.utility.DreadbotMath;

import java.util.ArrayList;
import java.util.List;

public class SparkDrive extends Subsystem {
	public static final CANSparkMaxLowLevel.MotorType K_MOTORTYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

	private final List<CANSparkMax> motors;

	public SparkDrive() {
		super("SparkDrive");
		this.motors = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			this.motors.add(new CANSparkMax(i + 1, K_MOTORTYPE));

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
	 * @param forwardAxisFactor    The forward factor of the drivetrain control.
	 * @param rotationAxisFactor   The rotational factor of the drivetrain control.
	 * @param finalValueMultiplier The final multiplier of the result of the
	 *                             function.
	 */
	public void tankDrive(double forwardAxisFactor,
	                      double rotationAxisFactor,
	                      final DriveMode driveMode) {
		tankDrive(forwardAxisFactor, rotationAxisFactor, driveMode, 0.2);
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
	                      final DriveMode driveMode,
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
		for (int i = 0; i < speedControllerOutputs.length; i++)
			speedControllerOutputs[i] *= driveMode.finalValueMultiplier;

		// Normalize the values to become between 1.0 and -1.0.
		speedControllerOutputs = DreadbotMath.normalizeValues(speedControllerOutputs);

		// Assign each value of the array to the motor output.
		for (int i = 0; i < speedControllerOutputs.length; i++)
			motors.get(i).set(speedControllerOutputs[i]);
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
