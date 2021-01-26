package frc.robot.subsystem;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DigitalInput;

public class Feeder {
	private CANSparkMax genevaDrive;
	private CANPIDController genevaControler;
	private CANEncoder genevaEncoder;
	private Solenoid punch;
	private DigitalInput genevaLimitSwitch;
	private DigitalInput punchLimitSwitch;
	private final int genevaSwitchPort = 9;
	private final int punchSwitchPort = 3;

	public Feeder(CANSparkMax genevaDrive, Solenoid punch){
		this.genevaDrive = genevaDrive;
		this.punch = punch;

		genevaDrive.setIdleMode(CANSparkMax.IdleMode.kBrake);
		genevaControler = genevaDrive.getPIDController();
		genevaEncoder = genevaDrive.getEncoder();
		genevaEncoder.setPosition(0);
		genevaLimitSwitch = new DigitalInput(genevaSwitchPort);
		punchLimitSwitch = new DigitalInput(punchSwitchPort);

		genevaControler.setP(.002);
		genevaControler.setI(1e-6);
		genevaControler.setD(.02);
		genevaControler.setFF(.000015);
		genevaControler.setIZone(0);
		genevaControler.setOutputRange(-1.0, 1.0);
	}

	public void setSpin(double power){
		genevaDrive.set(power);
	}
	public void setPunchExtension(boolean extended){
		punch.set(extended);
	}
	public boolean getPunchExtension(){
		return punch.get();
	}
	public boolean getGenevaSwitchState(){
		return !genevaLimitSwitch.get();
	}
	public boolean getPunchSwitchState(){
		return punchLimitSwitch.get();
	}
	public double getGenevaPosition(){
		return genevaEncoder.getPosition();
	}
	public void ExtendRetract(int millisecondsBetween){
		setPunchExtension(true);
		try{
			Thread.sleep(millisecondsBetween);
		}
		catch(Exception ex){
			System.err.println(ex);
		}
		setPunchExtension(false);
	}
}
