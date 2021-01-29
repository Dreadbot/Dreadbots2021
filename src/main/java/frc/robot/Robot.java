// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystem.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	//public CANSparkMax testMotor;
	public SparkDrive sparkDrive;

	public Joystick joystick;

  public Ultra sonic1;
 // public Ultra sonic2;
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    System.out.println("Hello World from RED 5 2021!");
    //testMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
    joystick = new Joystick(0);
    sparkDrive = new SparkDrive();
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
	}

  @Override
  public void teleopPeriodic() {
    Ultra.automatic();
    double a = sonic1.getRangeInches();

  //  sonic2.getRangeInches();
    //testMotor.set(0.3d);
    //System.out.println(joystick.getY());
    sparkDrive.tankDrive(joystick.getY(), joystick.getZ(), 0.2, 0.2);

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
