package frc.robot.gamestate;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.gamestate.routine.AutonSegment;
import frc.robot.gamestate.routine.AutonTimer;
import frc.robot.subsystem.SparkDrive;

import java.util.ArrayList;

/**
 * Logic Container for the Autonomous Period and Infinite Recharge at Home Challenges.
 */
public class Autonomous {
    // Routine Data
    private ArrayList<AutonSegment> autonSegments;
    private int autonRoutineIndex;
    private boolean autonCompleted;

    // Subsystems
    private SparkDrive sparkDrive;

    /**
     * Default Constructor (no-args)
     * @param sparkDrive
     */
    public Autonomous(SparkDrive sparkDrive) {
        this.sparkDrive = sparkDrive;
        this.autonSegments = new ArrayList<>();
        this.autonRoutineIndex = 0;
        this.autonCompleted = false;

        // Manually add segments to the routine (will be changed in the future)
        this.autonSegments.add(new AutonTimer(1.0));
        this.autonSegments.add(new AutonTimer(1.0));

        System.out.println("Starting to generate trajectory...");
        Pose2d startWaypoint = new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0));
        Pose2d endWaypoint = new Pose2d(Units.feetToMeters(2.5), Units.feetToMeters(2.5), Rotation2d.fromDegrees(90));

        ArrayList<Translation2d> interiorWaypoints = new ArrayList<Translation2d>();

        var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
                new SimpleMotorFeedforward(
                        SparkDrive.kSVolts,
                        SparkDrive.kVVoltSecondsPerMeter,
                        SparkDrive.kAVoltSecondsSquaredPerMeter),
                SparkDrive.kinematics,
                10
        );

        TrajectoryConfig trajectoryConfig = new TrajectoryConfig(SparkDrive.kMaxSpeedMetersPerSecond,
                SparkDrive.kMaxAccelerationMetersPerSecondSquared)
                .setKinematics(SparkDrive.kinematics)
                .addConstraint(autoVoltageConstraint);

        Trajectory trajectory = TrajectoryGenerator.generateTrajectory(
                startWaypoint,
                interiorWaypoints,
                endWaypoint,
                trajectoryConfig
        );

        System.out.println("Finished generating trajectory...");
        System.out.println("trajectory = " + trajectory);

        RamseteController controller = new RamseteController();

        Trajectory.State goal = trajectory.sample(0.0);
        System.out.println("goal = " + goal);

        sparkDrive.resetOdometry(trajectory.getInitialPose());
        sparkDrive.tankDriveVolts(0, 0);

        // ChassisSpeeds adjustedSpeeds = controller.calculate(currentRobotPosition, goal);
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
        if (autonCompleted)
            return;

        // Run the current segment's autonomousPeriodic() code.
        autonSegments.get(autonRoutineIndex).autonomousPeriodic();

        // Check to see if the current segment's task has been completed
        if (autonSegments.get(autonRoutineIndex).isComplete()) {
            // Move to the next segment of the routine
            autonRoutineIndex++;

            // If there are no more segments in the routine, stop the execution
            // of the autonomous logic.
            if (autonRoutineIndex >= autonSegments.size()) {
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
