package frc.robot.gamestate.routine;

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
import frc.robot.subsystem.SparkDrive;

import java.util.List;

public class AutonTrajectory extends AutonSegment {
	// Trajectory data
	private final Trajectory trajectory;

	// Trajectory Tracking Error Feedback PIDs
	private final PIDController leftPIDController;
	private final PIDController rightPIDController;

	// Motor Feedforward Calculator using characterized constants.
	private final SimpleMotorFeedforward simpleMotorFeedforward;

	// Trajectory Following Utils
	private final RamseteController controller;
	private DifferentialDriveWheelSpeeds previousWheelSpeeds;
	private double previousTime;

	// Time manager
	private final Timer timer;

	// Subsystems
	private final SparkDrive sparkDrive;

	public AutonTrajectory(SparkDrive sparkDrive, Pose2d initialPosition, Pose2d finalPosition) {
		this(sparkDrive, initialPosition, List.of(), finalPosition);
	}

	public AutonTrajectory(SparkDrive sparkDrive, Pose2d initialPosition, List<Translation2d> interiorWaypoints, Pose2d finalPosition) {
		this.sparkDrive = sparkDrive;

		// Setup Tracking PIDs
		leftPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);
		rightPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);

		// Create the Voltage Constraints for the Trajectory Tracking
		simpleMotorFeedforward = new SimpleMotorFeedforward(
			SparkDrive.kSVolts,
			SparkDrive.kVVoltSecondsPerMeter,
			SparkDrive.kAVoltSecondsSquaredPerMeter);
		var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
			simpleMotorFeedforward,
			SparkDrive.kinematics,
			7);

		// Create the Trajectory Configuration
		TrajectoryConfig trajectoryConfig = new TrajectoryConfig(
			SparkDrive.kMaxSpeedMetersPerSecond,
			SparkDrive.kMaxAccelerationMetersPerSecondSquared)
			.setKinematics(SparkDrive.kinematics)
			.addConstraint(autoVoltageConstraint);

		// Generate Trajectory from configuration
		trajectory = TrajectoryGenerator.generateTrajectory(
			initialPosition,
			interiorWaypoints,
			finalPosition,
			trajectoryConfig
		);

		sparkDrive.resetOdometry(trajectory.getInitialPose());

		controller = new RamseteController();

		timer = new Timer();
	}

	@Override
	public void autonomousInit() {
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
	}

	@Override
	public void autonomousPeriodic() {
		// Determine times and whether to continue
		final double currentTime = timer.get();
		final double deltaTime = currentTime - previousTime;
		if (currentTime >= trajectory.getTotalTimeSeconds()) {
			sparkDrive.tankDrive(0, 0);
			complete = true;
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

		// leftOutput *= 7; // V
		// rightOutput *= 7; // V

		System.out.println("leftFeedforward = " + leftFeedforward);
		System.out.println("rightFeedforward = " + rightFeedforward);

		System.out.println("leftOutput = " + leftOutput);
		System.out.println("rightOutput = " + rightOutput);

		System.out.println("sparkDrive.getPose() = " + sparkDrive.getPose());
		System.out.println("sparkDrive.getHeading() = " + sparkDrive.getHeading());


		sparkDrive.tankDriveVolts(leftOutput, rightOutput);

		previousTime = currentTime;
		previousWheelSpeeds = targetWheelSpeeds;
	}

	@Override
	public void disabledInit() {
		timer.stop();
	}
}
