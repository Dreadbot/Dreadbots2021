// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.gamestate.Autonomous;
import frc.robot.gamestate.Teleoperated;
import frc.robot.subsystem.*;
import frc.robot.utility.Constants;
import frc.robot.utility.DreadbotController;
import frc.robot.utility.logger.RobotLogger;

import java.util.ArrayList;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	// SUBSYSTEMS
	public SparkDrive sparkDrive;
	public Shooter shooter;
	public Intake intake;
	public Feeder feeder;
	public Manipulator manipulator;
	public Ultra sonic1;
	// public Ultra sonic2;

	// JOYSTICKS
	public DreadbotController primaryJoystick;
	public DreadbotController secondaryJoystick;

	// TESTING ONLY
	public ArrayList<Subsystem> testingSubsystems;
	public int currentTestingIndex;
	public boolean isTestingCompleted;

	// GAME STATE
	private Autonomous autonomous;
	private Teleoperated teleoperated;

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		RobotLogger.log("Starting Initialization of RedFive 2021...");

		// Joystick Initialization
		RobotLogger.log("Joystick Initialization...");
		primaryJoystick = new DreadbotController(0);
		secondaryJoystick = new DreadbotController(1);

		// Subsystem Initialization
		RobotLogger.log("SparkDrive Initialization...");
		sparkDrive = new SparkDrive();

		RobotLogger.log("Manipulator Initialization...");
		shooter = new Shooter();
		intake = new Intake();
		feeder = new Feeder();
		manipulator = new Manipulator(intake,
			feeder,
			shooter);

		RobotLogger.log("Ultrasonic Initialization...");
		sonic1 = new Ultra(Constants.ULTRA_PING_CHANNEL_ID, Constants.ULTRA_ECHO_CHANNEL_ID);
		// sonic2 = new Ultra(6, 7);

		// Game State Initialization
		RobotLogger.log("Game State Initialization...");
		teleoperated = new Teleoperated(primaryJoystick,
			secondaryJoystick,
			manipulator,
			sparkDrive);
		autonomous = new Autonomous(sparkDrive, teleoperated.getTeleopFunctions(), manipulator, teleoperated);

		// Testing Initialization
		RobotLogger.log("Testing Initialization...");
		testingSubsystems = new ArrayList<>();
		testingSubsystems.add(sparkDrive);
		//testingSubsystems.add(manipulator);

		RobotLogger.log("RedFive, standing by.");
		RobotLogger.log("GO DREADBOTS!");
	}

	@Override
	public void robotPeriodic() {
		sparkDrive.periodic();
	}

	@Override
	public void autonomousInit() {
	//	shooter.setVisionLight(true);
		autonomous.autonomousInit();
	}

	@Override
	public void autonomousPeriodic() {
		autonomous.autonomousPeriodic();
	}

	@Override
	public void teleopInit() {
		System.out.println("Robot.teleopInit\n");

		// SmartDashboard Setup
		SmartDashboard.putNumber("Shooter P", .0025);
		SmartDashboard.putNumber("Shooter I", 3.3e-7);
		SmartDashboard.putNumber("Shooter D", 0.03);
		SmartDashboard.putNumber("Shooter Target Speed", 3550);

		// Setup shooter for teleop
		shooter.setVisionLight(true);
		shooter.setHoodPercentOutput(0.25);
		shooter.setUpperBool(false);
		shooter.setLowerBool(false);
		shooter.setAimReadiness(false);

		sparkDrive.getGyroscope().reset();

		intake.deployIntake();
	}

	@Override
	public void teleopPeriodic() {
		// Drive
		teleoperated.teleopDrive();

		// Shooter
		shooter.setPID(SmartDashboard.getNumber("Shooter P", .0025),
			SmartDashboard.getNumber("Shooter I", 3.3e-7),
			SmartDashboard.getNumber("Shooter D", 0.03));
		SmartDashboard.putNumber("Shooter RPM", manipulator.getShooter().getShootingSpeed());

		shooter.hoodCalibration();

		SmartDashboard.putNumber("Shooter Velocity (Actual)", shooter.getShootingSpeed());
		teleoperated.teleopShooter();

		// Intake
		teleoperated.teleopIntake();

		// Ultrasonics;
		Ultra.automatic();
	}

	@Override
	public void disabledInit() {
		RobotLogger.reset();

		autonomous.disabledInit();

		shooter.setVisionLight(false);

		isTestingCompleted = false;
		for (Subsystem subsystem : testingSubsystems) {
			subsystem.setTestingCompleted(false);
			subsystem.setCurrentTestIndex(0);

			subsystem.getTimer().stop();
			subsystem.getTimer().reset();
		}
		currentTestingIndex = 0;
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void testInit() {
		sparkDrive.tankDrive(0, 0);
		manipulator.getShooter().setShootingPercentOutput(0);
		manipulator.genevaSetSpin(0);
		manipulator.getShooter().setHoodPercentOutput(0);

		System.out.println("Entering Robot Test Mode.");
		for (Subsystem i : testingSubsystems) {
			i.testInit();
		}
		isTestingCompleted = false;
	}

	@Override
	public void testPeriodic() {
		if (isTestingCompleted)
			return;

		if (currentTestingIndex >= testingSubsystems.size()) {
			isTestingCompleted = true;
			return;
		}

		testingSubsystems.get(currentTestingIndex).testPeriodic();

		if (testingSubsystems.get(currentTestingIndex).isTestingCompleted())
			currentTestingIndex++;
	}
}
