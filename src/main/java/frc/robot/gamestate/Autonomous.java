package frc.robot.gamestate;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.gamestate.routine.AutonSegment;
import frc.robot.gamestate.routine.AutonTimer;
import frc.robot.subsystem.SparkDrive;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
	private final SimpleMotorFeedforward simpleMotorFeedforward;
	// Routine Data
	private final ArrayList<AutonSegment> autonSegments;
	private final SparkDrive sparkDrive;
	private final RamseteController controller;
	private final Trajectory trajectory;
	private final Timer timer;
	private final PIDController leftPIDController;
	private final PIDController rightPIDController;
	private int autonRoutineIndex;
	private boolean autonCompleted;
	private DifferentialDriveWheelSpeeds previousWheelSpeeds;
	private double previousTime;

	/**
	 * Default Constructor (no-args)
	 */
	public Autonomous(SparkDrive sparkDrive) {
		this.autonSegments = new ArrayList<>();
		this.autonRoutineIndex = 0;
		this.autonCompleted = false;

		this.sparkDrive = sparkDrive;
		// Manually add segments to the routine (will be changed in the future)
		this.autonSegments.add(new AutonTimer(1.0));
		this.autonSegments.add(new AutonTimer(1.0));

		leftPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);
		rightPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);

		simpleMotorFeedforward = new SimpleMotorFeedforward(
			SparkDrive.kSVolts,
			SparkDrive.kVVoltSecondsPerMeter,
			SparkDrive.kAVoltSecondsSquaredPerMeter);
		var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
			simpleMotorFeedforward,
			SparkDrive.kinematics,
			6);

		TrajectoryConfig trajectoryConfig = new TrajectoryConfig(
			SparkDrive.kMaxSpeedMetersPerSecond,
			SparkDrive.kMaxAccelerationMetersPerSecondSquared)
			.setKinematics(SparkDrive.kinematics)
			.addConstraint(autoVoltageConstraint);

		trajectory = TrajectoryGenerator.generateTrajectory(
			new Pose2d(0, 0, new Rotation2d(0)),
			List.of(),
			new Pose2d(0.5, 0.5, new Rotation2d(90)),
			trajectoryConfig
		);

		sparkDrive.resetOdometry(trajectory.getInitialPose());

		controller = new RamseteController();

		timer = new Timer();
	}

	/**
	 * Called directly from Robot.autonomousInit() function. Initializes the first
	 * segment
	 */
	public void autonomousInit() {
		System.out.println("Autonomous.autonomousInit");

		previousTime = -1;
		var initialState = trajectory.sample(0);
		previousWheelSpeeds = SparkDrive.kinematics.toWheelSpeeds(
			new ChassisSpeeds(
				initialState.velocityMetersPerSecond,
				0,
				initialState.curvatureRadPerMeter * initialState.velocityMetersPerSecond));
		timer.reset();
		timer.start();
		leftPIDController.reset();
		rightPIDController.reset();

		// Call init method for first autonomous segment in the routine
//		autonSegments.get(autonRoutineIndex).autonomousInit();
	}

	/**
	 * Called directly from Robot.autonomousPeriodic() function. Runs the routine's segments
	 * in order of how they were added.
	 */
	public void autonomousPeriodic() {
		// Determine times and whether to continue
		final double currentTime = timer.get();
		final double deltaTime = currentTime - previousTime;
		if (currentTime >= trajectory.getTotalTimeSeconds()) {
			sparkDrive.tankDriveVolts(0, 0);
			return;
		}

		// First iteration setup
		if (previousTime < 0) {
			sparkDrive.tankDriveVolts(0, 0);
			previousTime = currentTime;
			return;
		}

		// Get the target and current wheel speeds for operations.
		var targetWheelSpeeds = SparkDrive.kinematics.toWheelSpeeds(
			controller.calculate(sparkDrive.getPose(), trajectory.sample(currentTime))
		);
		var currentWheelSpeeds = sparkDrive.getWheelSpeeds();

		// Calculate the feedforward in Volts.
		double leftFeedforward =
			simpleMotorFeedforward.calculate(targetWheelSpeeds.leftMetersPerSecond,
				(targetWheelSpeeds.leftMetersPerSecond - previousWheelSpeeds.leftMetersPerSecond) / deltaTime);
		double rightFeedforward =
			simpleMotorFeedforward.calculate(targetWheelSpeeds.rightMetersPerSecond,
				(targetWheelSpeeds.rightMetersPerSecond - previousWheelSpeeds.rightMetersPerSecond) / deltaTime);

		// Add in error calculated from the PID controllers.
		var leftOutput =
			leftFeedforward + leftPIDController.calculate(
				currentWheelSpeeds.leftMetersPerSecond,
				targetWheelSpeeds.leftMetersPerSecond);
		var rightOutput =
			rightFeedforward + rightPIDController.calculate(
				currentWheelSpeeds.rightMetersPerSecond,
				targetWheelSpeeds.rightMetersPerSecond);

		System.out.println("leftOutput = " + leftFeedforward);
		System.out.println("rightOutput = " + rightFeedforward);

		sparkDrive.tankDriveVolts(leftOutput, rightOutput);

		previousTime = currentTime;
		previousWheelSpeeds = targetWheelSpeeds;

		// // Prevent IndexOutOfBoundsExceptions and allows the robot to remain
		// // running after the routine is finished.
		// if(autonCompleted)
		// 	return;

		// // Run the current segment's autonomousPeriodic() code.
		// autonSegments.get(autonRoutineIndex).autonomousPeriodic();

		// // Check to see if the current segment's task has been completed
		// if(autonSegments.get(autonRoutineIndex).isComplete()) {
		// 	// Move to the next segment of the routine
		// 	autonRoutineIndex++;

		// 	// If there are no more segments in the routine, stop the execution
		// 	// of the autonomous logic.
		// 	if(autonRoutineIndex >= autonSegments.size()) {
		// 		// Prevents the autonomous logic from being run until the next time
		// 		// the autonomous period starts.
		// 		autonCompleted = true;
		// 		return;
		// 	}

		// 	// If there are more segments in the routine,
		// 	// call the next segment's init method.
		// 	autonSegments.get(autonRoutineIndex).autonomousInit();
		// }
	}

	/**
	 * Called directly from Robot.disabledInit() function. Resets the routine management
	 * data so that the routine can be run in the same on/off cycle of the robot.
	 */
	public void disabledInit() {
		// Performs a reset of all the routine data so it can be run multiple times
		// in the same on/off cycle of the robot.
		autonRoutineIndex = 0;
		autonCompleted = false;
		for (AutonSegment routine : autonSegments) {
			routine.setComplete(false);
			routine.disabledInit();
		}

		timer.stop();
	}

	public void addRoutine(AutonSegment a) {
		this.autonSegments.add(a);
	}
}
