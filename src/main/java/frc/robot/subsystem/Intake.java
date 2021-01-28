package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANPIDController;
import edu.wpi.first.wpilibj.Solenoid;

public class Intake {
	private CANSparkMax intakeMotor;
	private CANPIDController pidController;
	private Solenoid intakePin;

	public Intake(CANSparkMax intakeMotor, Solenoid intakePin){
		this.intakeMotor = intakeMotor;
		this.intakePin = intakePin;
		pidController = intakeMotor.getPIDController();

		pidController.setP(6e-5);
		pidController.setI(1e-6);
		pidController.setD(0.3);
		pidController.setIZone(0);
		pidController.setFF(.000015);
		pidController.setOutputRange(-1.0, 1.0);
	}
	public void setSpeed(double speed){
		pidController.setReference(speed, ControlType.kVelocity);
	}

	public void setPercentOutput(double percentOutput){
		intakeMotor.set(percentOutput);
	}

	public void deployIntake(){
		//this is true because true flips the solenoid from what is default,
		//and the pin should physically be setup so that it is extended by default
		intakePin.set(true);
	}
	public void Start(){
		intakeMotor.set(0.5d);
	}
	public void Stop(){
		intakeMotor.set(0.0d);
	}
}
