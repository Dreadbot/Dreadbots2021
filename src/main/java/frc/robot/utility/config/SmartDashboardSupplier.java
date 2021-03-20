package frc.robot.utility.config;

/**
 * Base class for suppliers to associated values in SmartDashboard/Shuffleboard.
 */
public abstract class SmartDashboardSupplier {
    /**
     * The shared, unique identification of the relationship between a number in memory and one in
     * SmartDashboard/Shuffleboard.
     */
    protected final String id;

    /**
     * Default constructor with the shared, unique identification.
     *
     * @param id The shared, unique identification of the two values
     */
    protected SmartDashboardSupplier(String id) {
        this.id = id;
    }

    /**
     * Standard getter for the shared, unique identifier
     * @return
     */
    public String getId() {
        return id;
    }
}
