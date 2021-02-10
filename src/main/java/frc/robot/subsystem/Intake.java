package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import com.revrobotics.CANPIDController;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.utility.Constants;

public class Intake {
	private CANSparkMax intakeMotor;
	private CANPIDController pidController;
	private Solenoid intakePin;

	public Intake() {
		this.intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR_ID, CANSparkMaxLowLevel.MotorType.kBrushless);
		this.pidController = intakeMotor.getPIDController();
		this.intakePin = new Solenoid(Constants.INTAKE_PIN_ID);

		this.pidController.setP(6e-5);
		this.pidController.setI(1e-6);
		this.pidController.setD(0.3);
		this.pidController.setIZone(0);
		this.pidController.setFF(.000015);
		this.pidController.setOutputRange(-1.0, 1.0);
	}

	public void setSpeed(double speed) {
		pidController.setReference(speed, ControlType.kVelocity);
	}

	public void setPercentOutput(double percentOutput) {
		intakeMotor.set(percentOutput);
	}

	public void deployIntake() {
		//this is true because true flips the solenoid from what is default,
		//and the pin should physically be setup so that it is extended by default
		intakePin.set(true);
	}

	public void Start() {
		intakeMotor.set(0.5d);
	}

	public void Stop() {
		intakeMotor.set(0.0d);
	}

	public CANSparkMax getIntakeMotor() {
		return intakeMotor;
	}

	public CANPIDController getPidController() {
		return pidController;
	}

	public Solenoid getIntakePin() {
		return intakePin;
	}
}
