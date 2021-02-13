package frc.robot.gamestate;

import frc.robot.Robot;
import frc.robot.gamestate.routine.AutonSegment;
import frc.robot.gamestate.routine.AutonTimer;
import frc.robot.subsystem.Ultra;

import java.util.ArrayList;

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
	// Routine Data
	private ArrayList<AutonSegment> autonSegments;
	private int autonRoutineIndex;
	private boolean autonCompleted;

	/**
	 * Default Constructor (no-args)
	 */
	public Autonomous() {
		this.autonSegments = new ArrayList<>();
		this.autonRoutineIndex = 0;
		this.autonCompleted = false;

		// Manually add segments to the routine (will be changed in the future)
		this.autonSegments.add(new AutonTimer(1.0));
		this.autonSegments.add(new AutonTimer(1.0));
	}

	/**
	 * Called directly from Robot.autonomousInit() function. Initializes the first
	 * segment
	 */
	public void autonomousInit() {
		System.out.println("Autonomous.autonomousInit");

		// Call init method for first autonomous segment in the routine
		autonSegments.get(autonRoutineIndex).autonomousInit();
	}

	/**
	 * Called directly from Robot.autonomousPeriodic() function. Runs the routine's segments
	 * in order of how they were added.
	 */
	public void autonomousPeriodic() {
		// Prevent IndexOutOfBoundsExceptions and allows the robot to remain
		// running after the routine is finished.
		if(autonCompleted)
			return;

		// Run the current segment's autonomousPeriodic() code.
		autonSegments.get(autonRoutineIndex).autonomousPeriodic();

		// Check to see if the current segment's task has been completed
		if(autonSegments.get(autonRoutineIndex).isComplete()) {
			// Move to the next segment of the routine
			autonRoutineIndex++;

			// If there are no more segments in the routine, stop the execution
			// of the autonomous logic.
			if(autonRoutineIndex >= autonSegments.size()) {
				// Prevents the autonomous logic from being run until the next time
				// the autonomous period starts.
				autonCompleted = true;
				return;
			}

			// If there are more segments in the routine,
			// call the next segment's init method.
			autonSegments.get(autonRoutineIndex).autonomousInit();
		}
	}

	/**
	 * Called directly from Robot.disabledInit() function. Resets the routine management
	 * data so that the routine can be run in the same on/off cycle of the robot.
	 */
	public void disabledInit() {
		// Performs a reset of all the routine data so it can be run multiple times
		// in the same on/off cycle of the robot.
		autonRoutineIndex = 0;
		autonCompleted = false;
		for(AutonSegment routine : autonSegments)
			routine.setComplete(false);
	}
}
