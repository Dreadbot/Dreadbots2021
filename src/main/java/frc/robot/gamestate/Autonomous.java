package frc.robot.gamestate;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.gamestate.routine.AutonSegment;
import frc.robot.gamestate.routine.AutonRoutine;
import frc.robot.gamestate.routine.AutonShoot;
import frc.robot.gamestate.routine.AutonTimer;
import frc.robot.gamestate.routine.AutonTrajectory;
import frc.robot.gamestate.routine.RotateToAngle;
import frc.robot.subsystem.Manipulator;
import frc.robot.subsystem.SparkDrive;
import frc.robot.utility.TeleopFunctions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
	// Routine Data
	private final HashMap<String, AutonRoutine> autonRoutines;

	private final ArrayList<AutonSegment> autonSegments;
	private final SparkDrive sparkDrive;
	private final TeleopFunctions teleopFunctions;
	private final Manipulator manipulator;
	private final Teleoperated teleoperated;
	private int autonRoutineIndex;
	private boolean autonCompleted;

	// SmartDashboard
	private final SendableChooser<String> autonChooser;

	/**
	 * Default Constructor (no-args)
	 */
	public Autonomous(SparkDrive sparkDrive, TeleopFunctions teleopFunctions, Manipulator manipulator, Teleoperated teleoperated) {
		this.teleoperated = teleoperated;
		this.manipulator = manipulator;
		this.sparkDrive = sparkDrive;
		this.teleopFunctions = teleopFunctions;
		
		this.autonRoutines = new HashMap<>();

		this.autonSegments = new ArrayList<>();
		this.autonRoutineIndex = 0;
		this.autonCompleted = false;

		this.autonChooser = new SendableChooser<>();
		this.autonChooser.setDefaultOption("Judge Demo Auton", "judge_demo");
		
		this.autonRoutines.put("judge_demo", new AutonRoutine(sparkDrive)
			// Trajectory straight forward
			.addSegment(new AutonTrajectory(
				sparkDrive, 
				new Pose2d(0, 0, new Rotation2d(0)),
				new Pose2d(Units.feetToMeters(7.5), 0, new Rotation2d(0))))
			
			// Shoot at the goal
			.addSegment(new AutonShoot(
				teleoperated, 
				manipulator, 
				3))
			
			// Trajectory through the chair obstacles
			.addSegment(new AutonTrajectory(
				sparkDrive,
				new Pose2d(Units.feetToMeters(7.5), 0, new Rotation2d(0)),
				new Pose2d(Units.feetToMeters(10.5), Units.feetToMeters(9), new Rotation2d(0))
			))
			
			// Rotate to -90
			.addSegment(new RotateToAngle(-90, sparkDrive, teleopFunctions))

			// Trajectory back through the chair obstacles
			.addSegment(new AutonTrajectory(
				sparkDrive,
				new Pose2d(Units.feetToMeters(10.5),Units.feetToMeters(9), new Rotation2d(-90)),
				new Pose2d(Units.feetToMeters(10.5), Units.feetToMeters(3), new Rotation2d(-90)) ))
			
			// Rotate back to 0
			.addSegment(new RotateToAngle(
				0, 
				sparkDrive, 
				teleopFunctions))
		);
	}

	/**
	 * Called directly from Robot.autonomousInit() function. Initializes the first
	 * segment
	 */
	public void autonomousInit() {
		System.out.println("Autonomous.autonomousInit");

		sparkDrive.getGyroscope().reset();

		// Call init method for first autonomous segment in the routine
		autonSegments.get(autonRoutineIndex).autonomousInit();

		manipulator.getShooter().setVisionLight(true);
		manipulator.getShooter().setHoodPercentOutput(0.25);
		manipulator.getShooter().setUpperBool(false);
		manipulator.getShooter().setLowerBool(false);
		manipulator.getShooter().setAimReadiness(false);
	}

	/**
	 * Called directly from Robot.autonomousPeriodic() function. Runs the routine's segments
	 * in order of how they were added.
	 */
	public void autonomousPeriodic() {
		 // Prevent IndexOutOfBoundsExceptions and allows the robot to remain
		 // running after the routine is finished.
		 manipulator.getShooter().hoodCalibration();
		 if(autonCompleted)
			return;

		 // Run the current segment's autonomousPeriodic() code.
		 autonSegments.get(autonRoutineIndex).autonomousPeriodic();

		 System.out.println("autonRoutineIndex = " + autonRoutineIndex);

		 // Check to see if the current segment's task has been completed
		 if(autonSegments.get(autonRoutineIndex).isComplete()) {
			// Move to the next segment of the routine
			autonRoutineIndex++;

			// If there are no more segments in the routine, stop the execution
			// of the autonomous logic.
			if(autonRoutineIndex >= autonSegments.size()) {
				// Prevents the autonomous logic from being run until the next time
				// the autonomous period starts.
				autonCompleted = true;
				return;
			}

			// If there are more segments in the routine,
			// call the next segment's init method.
			autonSegments.get(autonRoutineIndex).autonomousInit();
		 }
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
	}

	public void addRoutine(AutonSegment a) {
		this.autonSegments.add(a);
	}
}
