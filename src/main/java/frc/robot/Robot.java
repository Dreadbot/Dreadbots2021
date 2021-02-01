// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystem.*;


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
	public Joystick joystick;

	//GAME PIECE HANDLING
	public Shooter shooter;
	public Intake intake;
	public Feeder feeder;
	public Manipulator manipulator;

	//SOLENOIDS
	Solenoid punch;
	Solenoid intakePin;

	// ULTRASONICS
	Ultra sonic1;

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("Hello World from RED 5 2021!");
		//JOYSTICKS
		joystick = new Joystick(0);

		//MOTORS
		sparkDrive = new SparkDrive();
		genevaDrive = new CANSparkMax(Constants.MOTOR_ID_GENEVA, CANSparkMax.MotorType.kBrushless);
		intakeMotor = new CANSparkMax(Constants.MOTOR_ID_INTAKE, CANSparkMax.MotorType.kBrushless);
		//testMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);

		//SOLENOIDS
		punch = new Solenoid(Constants.SOLENOID_ID_PUNCH);
		intakePin = new Solenoid(Constants.SOLENOID_ID_INTAKE);

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
		// testMotor.set(0.3d);
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
	}

	@Override
	public void testPeriodic() {
	}
}
