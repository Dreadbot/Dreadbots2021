package frc.robot.subsystem;

import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.subsystem.SparkDrive.DriveMode;



public class Diagnostic {
    public Joystick js1;
    public Joystick js2;
    public SparkDrive sparkDrive;
    public CANSparkMax LF;
    public CANSparkMax RF;
    public CANSparkMax LR;
    public CANSparkMax RR;
    public Intake IN;
    public CANSparkMax geniva;
    public CANSparkMax shooty;
    public CANSparkMax aim;
    public Solenoid solup;
    public Solenoid solcolor;
  


    public void run(){
        if(Math.abs(js1.getRawAxis(1)) >= 0.025 || (Math.abs(js1.getRawAxis(2)) >= 0.025)){
           sparkDrive.tankDrive(js1.getY(), js1.getZ(),DriveMode.TURTLE);

        }
        else{
            sparkDrive.tankDrive(0,0);
        }
        if(js1.getRawButton(3)){
            shooty.set(0.5);
        }
        else{
            shooty.set(0);
        }
        if(js1.getRawButton(1)){
            IN.setSpeed(0.2);
        }
        else if(js1.getRawButton(2)){
            IN.setSpeed(-0.2);
        }
        else{
            IN.setSpeed(-0.2);
        }
        if(js1.getRawButton(5)){
            geniva.set(0.2);
        }
        else if(js1.getRawButton(6)){
            geniva.set(-0.2);
        }
        else{
            geniva.set(0);
        }
    }
}

