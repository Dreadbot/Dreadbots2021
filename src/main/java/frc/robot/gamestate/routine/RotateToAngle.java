package frc.robot.gamestate.routine;

import frc.robot.utility.TeleopFunctions;

public class RotateToAngle extends AutonSegment{

    private int turnToAngle;
    private TeleopFunctions teleopFunctions;

    public RotateToAngle(int turnToAngle, TeleopFunctions teleopFunctions) {
        this.turnToAngle = turnToAngle;
        this.teleopFunctions = teleopFunctions;
    }

    @Override
    public void autonomousInit() {
        // TODO Auto-generated method stub
    }

    @Override
    public void autonomousPeriodic() {
        teleopFunctions.WPITurnToAngle(turnToAngle);

        // TODO Auto-generated method stub
        if(teleopFunctions.getTurnStatus()) {
            teleopFunctions.setTurnStatus(false);
            complete = true;
        }
    }
    
}
