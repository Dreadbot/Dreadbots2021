package frc.robot.gamestate;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.gamestate.routine.AutonSegment;
import frc.robot.gamestate.routine.AutonTimer;
import frc.robot.gamestate.routine.AutonTrajectory;
import frc.robot.subsystem.SparkDrive;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
	// Routine Data
	private final ArrayList<AutonSegment> autonSegments;
	private final SparkDrive sparkDrive;
	private int autonRoutineIndex;
	private boolean autonCompleted;

	/**
	 * Default Constructor (no-args)
	 */
	public Autonomous(SparkDrive sparkDrive) {
		this.sparkDrive = sparkDrive;

		this.autonSegments = new ArrayList<>();
		this.autonRoutineIndex = 0;
		this.autonCompleted = false;

		// Manually add segments to the routine (will be changed in the future)
		this.autonSegments.add(new AutonTrajectory(
			sparkDrive,
			new Pose2d(0, 0, new Rotation2d(0)),
			new Pose2d(Units.feetToMeters(7.5), Units.feetToMeters(3), new Rotation2d(0))
		));
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
		for (AutonSegment routine : autonSegments) {
			routine.setComplete(false);
			routine.disabledInit();
		}
	}

	public void addRoutine(AutonSegment a) {
		this.autonSegments.add(a);
	}
}
