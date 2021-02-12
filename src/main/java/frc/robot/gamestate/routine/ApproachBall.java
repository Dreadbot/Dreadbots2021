package frc.robot.gamestate.routine;

import frc.robot.subsystem.Ultra;
import frc.robot.utility.DreadbotMath;
import frc.robot.subsystem.SparkDrive;
import java.lang.Math;

public class ApproachBall extends AutonSegment{
    
	public Ultra ultrasonic;
	private SparkDrive drive;

	public ApproachBall(Ultra ultrasonic, SparkDrive drive){
		this.ultrasonic = ultrasonic;
		this.drive = drive;
	}

	@Override
	public void autonomousInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void autonomousPeriodic() {
		double inches = ultrasonic.getRangeInches();//Subject to be changed to a vision method of getting distance

		double speed = 1.0 / Math.log(inches);
		speed = DreadbotMath.clampValue(speed, 0.2, 1.0);
		if(inches < 0.5) {//To be calibrated
			speed = 0;
			complete = true;
		}

		drive.tankDrive(speed, 0);
	}

}