// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystem.*;

import java.util.ArrayList;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	//public CANSparkMax testMotor;
	//

	//MOTORS
	public SparkDrive sparkDrive;
	public CANSparkMax genevaDrive;
	public CANSparkMax intakeMotor;

	//JOYSTICKS
	public Joystick joystick;

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
	private int kIntakeMotorID = 5;
	private int kGenevaMotorID = 6;
	private int kFlyWheelMotorID = 7;
	private int kAimMotorID = 8;
	private int kIntakePinID = 0;
	private int kPunchSolenoidID = 2;
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
		joystick = new Joystick(0);

		//MOTORS
		sparkDrive = new SparkDrive();
		genevaDrive = new CANSparkMax(kGenevaMotorID, CANSparkMax.MotorType.kBrushless);
		intakeMotor = new CANSparkMax(kIntakeMotorID, CANSparkMax.MotorType.kBrushless);

		//SOLENOIDS
		punch = new Solenoid(kPunchSolenoidID);
		intakePin = new Solenoid(kIntakePinID);

		shooter = new Shooter();
		intake = new Intake(intakeMotor, intakePin);
		feeder = new Feeder(genevaDrive, punch);
		manipulator = new Manipulator(intake, feeder, shooter);

		sonic1 = new Ultra(6, 7);
		// sonic2 = new Ultra(6, 7);
	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		System.out.println("Starting Teleop");
		// shooter.restoreFactoryDefaults();
		shooter.setHoodPercentOutput(0.25);
		shooter.setUpperBool(false);
		shooter.setLowerBool(false);
		shooter.setAimReadiness(false);
	}

	@Override
	public void teleopPeriodic() {
		//testMotor.set(0.3d);
		// System.out.println(joystick.getY());
		sparkDrive.tankDrive(joystick.getY(), joystick.getZ());

		shooter.hoodCalibration();

		if (joystick.getRawButton(1)) {
			manipulator.prepareShot(2500, 0.75);
		} else {
			shooter.setShootingPercentOutput(0);
		}

		Ultra.automatic();
		double a = sonic1.getRangeInches();
		System.out.println(a);
	}

	@Override
	public void disabledInit() {
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void testInit() {
		System.out.println("Entering Robot Test Mode.");

		testingSubsystems = new ArrayList<>();
		testingSubsystems.add(sparkDrive);
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
	}
}
