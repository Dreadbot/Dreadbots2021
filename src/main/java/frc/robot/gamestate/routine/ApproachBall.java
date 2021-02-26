package frc.robot.gamestate.routine;

import frc.robot.subsystem.Ultra;
import frc.robot.utility.DreadbotMath;
import frc.robot.utility.TeleopFunctions;
import frc.robot.subsystem.SparkDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.Math;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

public class ApproachBall extends AutonSegment{
    
	private Ultra ultrasonic;
	private SparkDrive drive;
	private double distance = 0;

	public ApproachBall(Ultra ultrasonic, SparkDrive drive){
		this.ultrasonic = ultrasonic;
		this.drive = drive;
	}
	public ApproachBall(double distance, SparkDrive drive){
		this.distance = distance;
	}	

	@Override
	public void autonomousInit() {
		SmartDashboard.putNumber("Approach Ball: Speed Scale", 1.0);
	}

	@Override
	public void autonomousPeriodic() {
		if(ultrasonic != null){
			double inches = ultrasonic.getRangeInches();//Subject to be changed to a vision method of getting distance
			double speed = (1.0 / Math.log(inches)) * SmartDashboard.getNumber("Approach Ball: Speed Scale", 1.0);

			speed = DreadbotMath.clampValue(speed, 0.2, 1.0);

			if(inches < 0.5) {//To be calibrated
				speed = 0;
				complete = true;
			}

			drive.tankDrive(speed, 0);
		}
		else{
			for(CANSparkMax motor : drive.getMotors()){
				//Need to figure out gear ratio / wheel cicumfrence in order to use this
				//motor.getPIDController().setReference(distance, ControlType.kPosition)
			}
		}
		
	}

	@Override
	public void disabledInit(){
		for(CANSparkMax motor : drive.getMotors()){
			motor.set(0);
		}
	}

}