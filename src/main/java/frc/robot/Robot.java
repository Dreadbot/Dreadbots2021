// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;

import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystem.Shooter;
import frc.robot.subsystem.SparkDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	public CANSparkMax testMotor;
	
	public SparkDrive sparkDrive;
	public Shooter shooter;

	public Joystick joystick;

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("Hello World from RED 5 2021!");
		testMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
		joystick = new Joystick(0);
		
		sparkDrive = new SparkDrive();
		shooter = new Shooter();
	}

	@Override
	public void robotPeriodic() {}

	@Override
	public void autonomousInit() {}

	@Override
	public void autonomousPeriodic() {}

	@Override
	public void teleopInit() {
		System.out.println("Starting Teleop");
	}

	@Override
	public void teleopPeriodic() {
		//testMotor.set(0.3d);
		System.out.println(joystick.getY());
		sparkDrive.tankDrive(joystick.getY(), joystick.getZ(), 0.2, 0.2);
	}

	@Override
	public void disabledInit() {}

	@Override
	public void disabledPeriodic() {}

	@Override
	public void testInit() {}

	@Override
	public void testPeriodic() {}
}
