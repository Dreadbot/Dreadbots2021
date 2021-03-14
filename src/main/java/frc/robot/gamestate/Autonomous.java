package frc.robot.gamestate;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

	private final SparkDrive sparkDrive;
	private final TeleopFunctions teleopFunctions;
	private final Manipulator manipulator;
	private final Teleoperated teleoperated;

	// SmartDashboard
	private final SendableChooser<String> autonChooser;
	private String selectedRoutine;

	/**
	 * Default Constructor (no-args)
	 */
	public Autonomous(SparkDrive sparkDrive, TeleopFunctions teleopFunctions, Manipulator manipulator, Teleoperated teleoperated) {
		this.teleoperated = teleoperated;
		this.manipulator = manipulator;
		this.sparkDrive = sparkDrive;
		this.teleopFunctions = teleopFunctions;
		
		this.autonRoutines = new HashMap<>();

		this.selectedRoutine = "judge_demo";

		this.autonChooser = new SendableChooser<>();
		this.autonChooser.setDefaultOption("Judge Demo Auton", selectedRoutine);
		
		this.autonRoutines.put(selectedRoutine, new AutonRoutine(sparkDrive)
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

		SmartDashboard.putData(autonChooser);
	}

	/**
	 * Called directly from Robot.autonomousInit() function. Initializes the first
	 * segment
	 */
	public void autonomousInit() {
		System.out.println("Autonomous.autonomousInit");

		selectedRoutine = autonChooser.getSelected();

		sparkDrive.getGyroscope().reset();

		// Call init method for first autonomous segment in the routine
		autonRoutines.get(selectedRoutine).autonomousInit();

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

		 // Run the current segment's autonomousPeriodic() code.
		 autonRoutines.get(selectedRoutine).autonomousPeriodic();
	}

	/**
	 * Called directly from Robot.disabledInit() function. Resets the routine management
	 * data so that the routine can be run in the same on/off cycle of the robot.
	 */
	public void disabledInit() {
		// Performs a reset of all the routine data so it can be run multiple times
		// in the same on/off cycle of the robot.
		autonRoutines.get(selectedRoutine).disabledInit();
	}
}
