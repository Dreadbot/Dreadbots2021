// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utility.DreadbotController;
import frc.robot.gamestate.Teleoperated;
import frc.robot.subsystem.*;
import frc.robot.utility.Constants;
import frc.robot.utility.TeleopFunctions;

import java.util.ArrayList;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	//MOTORS
	public SparkDrive sparkDrive;
	public CANSparkMax genevaDrive;
	public CANSparkMax intakeMotor;
	//public CANSparkMax testMotor;

	//JOYSTICKS
	public DreadbotController primaryJoystick;
	public DreadbotController secondaryJoystick;

	//GAME PEICE HANDLING
	public Shooter shooter;
	public Intake intake;
	public Feeder feeder;
	public Manipulator manipulator;
	// TESTING ONLY
	public ArrayList<Subsystem> testingSubsystems;
	public int currentTestingIndex;
	public boolean isTestingCompleted;
	public Ultra sonic1;
	//SOLENOIDS
	Solenoid punch;
	Solenoid intakePin;
	// GAME STATE
	private Teleoperated teleoperated;
	private TeleopFunctions teleopFunctions;

	// public Ultra sonic2;

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("Hello World from RED 5 2021!");
		//testMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
		//JOYSTICKS

		primaryJoystick = new DreadbotController(0);
		secondaryJoystick = new DreadbotController(1);

		//MOTORS
		sparkDrive = new SparkDrive();
		genevaDrive = new CANSparkMax(Constants.GENEVA_MOTOR_ID, CANSparkMax.MotorType.kBrushless);
		intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_ID, CANSparkMax.MotorType.kBrushless);

		//SOLENOIDS
		punch = new Solenoid(Constants.PUNCH_SOLENOID_ID);
		intakePin = new Solenoid(Constants.INTAKE_PIN_ID);

		shooter = new Shooter();
		intake = new Intake(intakeMotor, intakePin);
		feeder = new Feeder(genevaDrive, punch);
		manipulator = new Manipulator(intake, feeder, shooter);

		// GAME STATE
		teleopFunctions = new TeleopFunctions(secondaryJoystick, manipulator, sparkDrive);
		teleoperated = new Teleoperated(primaryJoystick,
			secondaryJoystick,
			manipulator,
			sparkDrive,
			teleopFunctions);

		sonic1 = new Ultra(6, 7);
		// sonic2 = new Ultra(6, 7);

		testingSubsystems = new ArrayList<>();
		testingSubsystems.add(sparkDrive);
		//testingSubsystems.add(manipulator);
	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void autonomousInit() {
		shooter.setVisionLight(true);
	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {
		SmartDashboard.putNumber("Shooter P", .0025);
		SmartDashboard.putNumber("Shooter I", 3.3e-7);
		SmartDashboard.putNumber("Shooter D", 0.03);
		SmartDashboard.putNumber("Shooter Target Speed", 3550);
		System.out.println("Starting Teleop");
		shooter.setVisionLight(true);
		shooter.restoreFactoryDefaults();
		shooter.setHoodPercentOutput(0.25);
		shooter.setUpperBool(false);
		shooter.setLowerBool(false);
		shooter.setAimReadiness(false);
		intake.deployIntake();
	}

	@Override
	public void teleopPeriodic() {
		//testMotor.set(0.3d);
		// System.out.println(primaryJoystick.getY());
		sparkDrive.tankDrive(primaryJoystick.getYAxis(), primaryJoystick.getZAxis());
		shooter.setPID(SmartDashboard.getNumber("Shooter P", .0025),
			SmartDashboard.getNumber("Shooter I", 3.3e-7),
			SmartDashboard.getNumber("Shooter D", 0.03));
		SmartDashboard.putNumber("Shooter RPM", manipulator.getShooter().getShootingSpeed());

		shooter.hoodCalibration();

		// if (primaryJoystick.getRawButton(Constants.X_BUTTON)) {
		// 	manipulator.prepareShot(2500, 0.75);
		// } else {
		// 	shooter.setShootingPercentOutput(0);
		// }
		// if(primaryJoystick.getRawButton(Constants.Y_BUTTON)){
		// 	manipulator.continuousShoot(0.5, 0.75, 3550);
		// } else {
		// 	// feeder.setPunchExtension(false);
		// 	manipulator.resetManipulatorElements();
		// }

		if (primaryJoystick.isRightBumperPressed()) {
			manipulator.sensorAdvanceGeneva(true, true);
		} else if (primaryJoystick.isLeftBumperPressed()) {
			manipulator.sensorAdvanceGeneva(true, false);
		} else {
			manipulator.sensorAdvanceGeneva(false, false);
		}

		if (primaryJoystick.isXButtonPressed()) {
			intake.setSpeed(-4000);
		} else if (primaryJoystick.isAButtonPressed()) {
			intake.setSpeed(4000);
		} else {
			intake.setPercentOutput(0);
		}

		SmartDashboard.putNumber("Shooter Velocity (Actual)", shooter.getShootingSpeed());
		teleoperated.teleopShooter();
		Ultra.automatic();
		double a = sonic1.getRangeInches();
		// System.out.println(a);
	}

	@Override
	public void disabledInit() {
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
