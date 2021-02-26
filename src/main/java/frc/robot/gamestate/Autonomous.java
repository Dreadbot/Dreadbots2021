package frc.robot.gamestate;

import edu.wpi.first.wpilibj.Spark;
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

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
	// Routine Data
	private ArrayList<AutonSegment> autonSegments;
	private int autonRoutineIndex;
	private boolean autonCompleted;

	private SparkDrive sparkDrive;

	private RamseteController controller;
	private final SimpleMotorFeedforward simpleMotorFeedforward;
	private Trajectory trajectory;
	private Timer timer;
	private PIDController leftPIDController;
	private PIDController rightPIDController;

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

		System.out.println("Starting to generate trajectory...");
		Pose2d startWaypoint = new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0));
		Pose2d endWaypoint = new Pose2d(Units.feetToMeters(2.5), Units.feetToMeters(2.5), Rotation2d.fromDegrees(90));

		leftPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);
		rightPIDController = new PIDController(SparkDrive.kPDriveVel, 0, 0);

		ArrayList<Translation2d> interiorWaypoints = new ArrayList<Translation2d>();

		simpleMotorFeedforward = new SimpleMotorFeedforward(
			SparkDrive.kSVolts,
			SparkDrive.kVVoltSecondsPerMeter,
			SparkDrive.kAVoltSecondsSquaredPerMeter);
		var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(simpleMotorFeedforward,
			SparkDrive.kinematics, 
			10);

		TrajectoryConfig trajectoryConfig = new TrajectoryConfig(1.0, 1.5)
		.setKinematics(SparkDrive.kinematics)
		.addConstraint(autoVoltageConstraint);

		trajectory = TrajectoryGenerator.generateTrajectory(
			startWaypoint,
			interiorWaypoints,
			endWaypoint,
			trajectoryConfig
		);

		System.out.println("Finished generating trajectory...");
		System.out.println("trajectory = " + trajectory);

		controller = new RamseteController();

		Trajectory.State goal = trajectory.sample(0.0);
		System.out.println("goal = " + goal);

		sparkDrive.resetOdometry(trajectory.getInitialPose());

		timer = new Timer();
		// ChassisSpeeds adjustedSpeeds = controller.calculate(currentRobotPosition, goal);
	}

	/**
	 * Called directly from Robot.autonomousInit() function. Initializes the first
	 * segment
	 */
	public void autonomousInit() {
		System.out.println("Autonomous.autonomousInit");

		previousTime = -1;
		var initialState = trajectory.sample(0.0d);
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

	// TODO CLEANUP
	private DifferentialDriveWheelSpeeds previousWheelSpeeds;
	private double previousTime;

	/**
	 * Called directly from Robot.autonomousPeriodic() function. Runs the routine's segments
	 * in order of how they were added.
	 */
	public void autonomousPeriodic() {
		System.out.println("Autonomous.autonomousPeriodic");

		final double currentTime = timer.get();
		final double deltaTime = currentTime - previousTime;
		if(currentTime >= trajectory.getTotalTimeSeconds())
			return;

		System.out.println("here1");

		if(previousTime < 0) {
			sparkDrive.tankDriveVolts(0, 0);
			previousTime = currentTime;
			return;
		}

		System.out.println("here2");

		var targetWheelSpeeds = SparkDrive.kinematics.toWheelSpeeds(
			controller.calculate(sparkDrive.getPose(), trajectory.sample(currentTime))
		);
		var currentWheelSpeeds = sparkDrive.getWheelSpeeds();

		System.out.println("here3");

		var left = targetWheelSpeeds.leftMetersPerSecond;
		var right = targetWheelSpeeds.rightMetersPerSecond;

		System.out.println("here4");

		double leftFeedforward =
			simpleMotorFeedforward.calculate(left,
				(left - previousWheelSpeeds.leftMetersPerSecond) / deltaTime);
		double rightFeedforward =
			simpleMotorFeedforward.calculate(right,
				(right - previousWheelSpeeds.rightMetersPerSecond) / deltaTime);

		System.out.println("here5");

		var leftOutput =
			leftFeedforward + leftPIDController.calculate(
				currentWheelSpeeds.leftMetersPerSecond,
				targetWheelSpeeds.leftMetersPerSecond);

		var rightOutput =
			rightFeedforward + rightPIDController.calculate(
				currentWheelSpeeds.rightMetersPerSecond,
				targetWheelSpeeds.rightMetersPerSecond);

		System.out.println("here6");

		System.out.println("left: " + leftOutput + ", right: " + rightOutput);
		sparkDrive.tankDriveVolts(0.5, 0.5);

		System.out.println("here7");

		previousTime = currentTime;
		previousWheelSpeeds = targetWheelSpeeds;

		System.out.println("here8");

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
		for(AutonSegment routine : autonSegments){
			routine.setComplete(false);
			routine.disabledInit();
		}
			
	}

	public void addRoutine(AutonSegment a){
		this.autonSegments.add(a);
	}
}
