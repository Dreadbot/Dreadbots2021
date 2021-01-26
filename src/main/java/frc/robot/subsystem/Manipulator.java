package frc.robot.subsystem;

public class Manipulator {
    private Intake intake;
    private Feeder feeder;
    private Shooter shooter;
    private enum shooterStates{RAMPING, PUNCHING, RETRACTING, ADVANCE, ADVANCING};
    private enum genevaStates{MOVE, MOVING, STOPPED, FORWARD, BACKWARD};
    private genevaStates genevaState = genevaStates.STOPPED;
    private genevaStates genevaDirection = genevaStates.FORWARD;
    private shooterStates shooterState;
    private shooterStates lastShooterState;
    private int stateChangeCounter;
    private final int countsToExtend = 5; 
    private int numPunches;

    public Manipulator(Intake intake, Feeder feeder, Shooter shooter){
        this.intake = intake;
        this.feeder = feeder;
        this.shooter = shooter;
        shooterState = shooterStates.RAMPING;
        numPunches = 0;
        stateChangeCounter = 0;
    }

    public void prepareShot(int rpm, double aimPosition){
        shooter.shoot(-rpm);
        shooter.setHoodPosition(aimPosition);
    }

    public double getSelectedRPM(double inches){
        inches /= 12;
        //below math really should have been commented in the C++ original, but wasn't
        return ((-0.0029 * inches * inches) + (0.188026 * inches) + 1.7676)*1000;
    }

    public double getSelectedHoodPosition(double inches){
        inches /= 12;
        //below math really should have been commented in the C++ original, but wasn't
        return ((-0.0941 * inches * inches) + (4.96271 * inches) + 2.08)/100;
    }

    public void continuousShoot(double aimPosition, double genevaSpeed, int shootingRPM){
        //finite state machine logic

        //Find difference between intended speed and actual speed
        int speedDifference = Math.abs(shooter.getShootingSpeed()) - shootingRPM;
        
        //if speed is within acceptable margin of error, switch to punching
        if(shooterState == shooterStates.RAMPING && speedDifference < 300 && speedDifference > 0){
            shooterState = shooterStates.PUNCHING;
        }
        else if(shooterState == shooterStates.RAMPING && speedDifference > -100 && speedDifference < 0){
            shooterState = shooterStates.PUNCHING;
        }
        //Change state based on a counter so the punch has enough time to extend
        else if(shooterState == shooterStates.PUNCHING && stateChangeCounter > countsToExtend){
            shooterState = shooterStates.RETRACTING;
            stateChangeCounter = 0;
        }
        //Change state based on a limit switch so that the solenoid has time to retract
        else if(shooterState == shooterStates.RETRACTING && feeder.getPunchSwitchState()){
            shooterState = shooterStates.ADVANCE;
            stateChangeCounter = 0;
        }
        //Change state if the geneva drive has rotated away from the limit switch
        else if(shooterState == shooterStates.ADVANCE && !feeder.getGenevaSwitchState()){
            shooterState = shooterStates.ADVANCING;
        }
        //Change state if the geneva drive has rotated back to the limit switch
        else if(shooterState == shooterStates.ADVANCING && feeder.getGenevaSwitchState()){
            shooterState = shooterStates.RAMPING;
        }

        //Choose behavior based on the FSM state
        switch(shooterState){
            case RAMPING:
                feeder.setSpin(0);
                break;
            case PUNCHING:
                feeder.setPunchExtension(true);
                stateChangeCounter++;
                break;
            case RETRACTING:    
                if(lastShooterState != shooterStates.RETRACTING){
                    ++numPunches;
                }
                feeder.setPunchExtension(false);    
                break;
            case ADVANCE:
            case ADVANCING:
                feeder.setSpin(genevaSpeed);
                break;
        } 
        shooter.setHoodPosition(aimPosition);
        shooter.shoot(-shootingRPM);
        shooter.setVisionLEDRingEnabled(true);

        lastShooterState = shooterState;
    }

    public void resetManipulatorElements(){
        //This function should be called continuously if the system is not shooting or collecting power cells
        // this function will get the system back into a state where the punch is retracted and the geneva gear is aligned in order to be able to begin the FSM again

        numPunches = 0;
        shooter.setVisionLEDRingEnabled(true);

        //If the punch is out, retract it
        if(feeder.getPunchExtension()){
            feeder.setPunchExtension(false);
        }
        //Then, once its retracted, if the geneva gear isn't at the limit switch, turn it slowly
        else if(!feeder.getPunchExtension() && !feeder.getGenevaSwitchState()){
            //This is commented out in the C++ original and I'm not sure why
            //feeder.setSpin(0.2);
        }
        //Once it reaches the limit switch, stop it
        else{
            feeder.setSpin(0);
        }
        shooterState = shooterStates.RAMPING;
        if(shooter.getAimReadiness()){
            shooter.setHoodPosition(0);
        }
    }

    public int getNumPunches(){
        return numPunches;
    }

    public void sensorAdvanceGeneva(boolean spin, boolean forward){
        double genevaSpeed = 0.4;
        if(genevaState == genevaStates.STOPPED && spin){
            if(forward){
                feeder.setSpin(-genevaSpeed);
                genevaDirection = genevaStates.FORWARD;
            }
            else{
                feeder.setSpin(genevaSpeed);
                genevaDirection = genevaStates.BACKWARD;
            }
            genevaState = genevaStates.MOVE;
        }
        else if(genevaState == genevaStates.MOVE && !feeder.getGenevaSwitchState()){
            genevaState = genevaStates.MOVING;
        }
        else if(genevaState == genevaStates.MOVING && feeder.getGenevaSwitchState()){
            feeder.setSpin(0);
            genevaState = genevaStates.STOPPED;
        }

        if(genevaState == genevaStates.MOVE || genevaState == genevaStates.MOVING){
            if(genevaDirection == genevaStates.FORWARD){
                feeder.setSpin(-genevaSpeed);
            }
            else{
                feeder.setSpin(genevaSpeed);
            }
        }
    }

    public void genevaSetSpin(double power){
        feeder.setSpin(power);
    }

    public int getSensorAdvanceGenevaState(){
        return genevaState.ordinal();
    }

    Intake getIntake(){
        return intake;
    }
    Feeder getFeeder(){
        return feeder;
    }
    Shooter getShooter(){
        return shooter;
    }
    
}
