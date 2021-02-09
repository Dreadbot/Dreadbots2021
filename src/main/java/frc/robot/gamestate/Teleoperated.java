package frc.robot.gamestate;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystem.Manipulator;
import frc.robot.subsystem.SparkDrive;
import frc.robot.subsystem.SparkDrive.DriveMode;
import frc.robot.utility.Constants;
import frc.robot.utility.DreadbotController;
import frc.robot.utility.TeleopFunctions;

public class Teleoperated {
	private DreadbotController primaryJoystick;
	private DreadbotController secondaryJoystick;
	private Manipulator manipulator;
	private SparkDrive sparkDrive;
	private TeleopFunctions teleopFunctions;
	private AimShootStates aimShootState;

	final int maxAimCounts;
	int aimCounts;

	double selectedAngle = 0;
	int lastCount = 0;
	int staleCount = 0;

	double distance = 120;
	double rotSpeed = 0;

	public Teleoperated(DreadbotController primaryJoystick,
	                    DreadbotController secondaryJoystick,
	                    Manipulator manipulator,
	                    SparkDrive sparkDrive,
	                    TeleopFunctions teleopFunctions) {
		this.primaryJoystick = primaryJoystick;
		this.secondaryJoystick = secondaryJoystick;

		this.manipulator = manipulator;
		this.sparkDrive = sparkDrive;
		this.teleopFunctions = teleopFunctions;

		aimShootState = AimShootStates.AIMING;
		maxAimCounts = 150;
		aimCounts = 0;
	}

	public void initIntake() {
		manipulator.getIntake().deployIntake();
	}

	public void initDrive() {
		sparkDrive.getGyroscope().zeroYaw();
	}

	public void teleopIntake() {
		if (secondaryJoystick.isXButtonPressed()) {
			manipulator.getIntake().setSpeed(-4000);
		} else if (secondaryJoystick.isAButtonPressed()) {
			manipulator.getIntake().setSpeed(4000);
		} else {
			manipulator.getIntake().setPercentOutput(0);
		}
	}

	public void teleopDrive() {
		//TODO Write this better
		DriveMode driveMode = DriveMode.NORMAL;
		driveMode = primaryJoystick.isRightTriggerPressed() ? DriveMode.TURBO : DriveMode.NORMAL;
		driveMode = primaryJoystick.isRightBumperPressed() ? DriveMode.TURTLE : DriveMode.NORMAL;

		sparkDrive.tankDrive(primaryJoystick.getYAxis(),
			primaryJoystick.getZAxis(),
			driveMode);
	}

	public void teleopShooter() {
		SmartDashboard.putNumber("Current Angle", sparkDrive.getGyroscope().getYaw());
		double hood_position = SmartDashboard.getNumber("Hood Position", 0.5);
		double pValue = SmartDashboard.getNumber("Turn P Value", 0.002);

		if (SmartDashboard.getNumber("detectionCount", lastCount) == lastCount)
			staleCount++;
		else
			staleCount = 0;

		if (aimShootState == AimShootStates.AIMING) {
			distance = SmartDashboard.getNumber("selectedDistance", 120);
		}
		//update the latest count, for use on next loop iteration
		lastCount = (int) SmartDashboard.getNumber("detectionCount", lastCount);

		//if we are done turning (not currently turning), then update angle from vision
		if (teleopFunctions.getTurnStatus()) {
			selectedAngle = (sparkDrive.getGyroscope().getYaw() - SmartDashboard.getNumber("selectedAngle", 0.0));
		}
		//Only turn and shoot when we hold the button, and we have seen the target recently

		if (secondaryJoystick.isYButtonPressed()) {
			double shooting_hood_position = SmartDashboard.getNumber("Hood Position", 0.5);
			System.out.println("Cont Shooting");
			manipulator.continuousShoot(shooting_hood_position, 0.4, SmartDashboard.getNumber("Target Speed", 0));
			SmartDashboard.putNumber("camNumber", 0);
		} else if (secondaryJoystick.isBButtonPressed() && staleCount < 5) {
			aimingContinuousShoot(distance, pValue, selectedAngle, 0.4);
			SmartDashboard.putNumber("camNumber", 0);
			staleCount = 0;
		} else if (secondaryJoystick.isBButtonPressed()) {
			SmartDashboard.putNumber("camNumber", 0);
		} else if (secondaryJoystick.isRightBumperPressed()) {
			manipulator.sensorAdvanceGeneva(true, true);
		} else if (secondaryJoystick.isLeftBumperPressed()) {
			manipulator.sensorAdvanceGeneva(true, false);
		} else if (manipulator.getSensorAdvanceGenevaState() == 2) {
			//std::cout << "Reseting" << std::endl;
			manipulator.resetManipulatorElements();
			teleopFunctions.setTurnStatus(true);
			aimCounts = 0;
			aimShootState = AimShootStates.AIMING;
			manipulator.sensorAdvanceGeneva(false, false);
			rotSpeed = 0;
		} else {
			SmartDashboard.putNumber("camNumber", 1);
		}
	}

	public void aimingContinuousShoot(double distance, double targetAngle, double genevaSpeed) {
		double rpm = manipulator.getSelectedRPM(distance);
		double hoodPosition = manipulator.getSelectedHoodPosition(distance);

		aimShootState = (aimCounts < maxAimCounts) ? AimShootStates.AIMING : AimShootStates.SHOOTING;

		switch (aimShootState) {
			case AIMING:
				rotSpeed = teleopFunctions.calculateTurnToAngle(targetAngle);
				manipulator.prepareShot(rpm, hoodPosition);
				break;
			case SHOOTING:
				sparkDrive.stop();
				manipulator.continuousShoot(hoodPosition, genevaSpeed, rpm);
				break;
		}
		aimCounts++;
	}

	public void aimingContinuousShoot(double rpm, double hoodPosition, double targetAngle, double genevaSpeed) {
		SmartDashboard.putNumber("aim counts", aimCounts);

		aimShootState = (aimCounts < maxAimCounts) ? AimShootStates.AIMING : AimShootStates.SHOOTING;

		switch (aimShootState) {
			case AIMING:
				teleopFunctions.WPITurnToAngle(targetAngle);
				manipulator.prepareShot(rpm, hoodPosition);
				break;
			case SHOOTING:
				sparkDrive.stop();
				manipulator.continuousShoot(hoodPosition, genevaSpeed, rpm);
				break;
		}
		aimCounts++;
	}

	public void resetAimCounts() {
		aimCounts = 0;
	}

	private enum AimShootStates {
		AIMING,
		SHOOTING;
	}
}
