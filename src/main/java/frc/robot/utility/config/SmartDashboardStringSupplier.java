package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Supplier to a String object configurable on the SmartDashboard/Shuffleboard on the drive computer.
 */
public class SmartDashboardStringSupplier extends SmartDashboardSupplier {
    /**
     * Default or starting value on SmartDashbaord/Shuffleboard.
     */
    private final String defaultValue;

    /**
     * The current String value in memory since the last sync with SmartDashbaord/Shuffleboard
     */
    private String currentValue;

    /**
     * Constructor with id of configurable setting in SmartDashboard/Shuffleboard and default value.
     *
     * @param id Id of the configurable setting in SmartDashboard/Shuffleboard.
     * @param defaultValue The default value to put in SmartDashboard/Shuffleboard.
     */
    public SmartDashboardStringSupplier(String id, String defaultValue) {
        super(id);

        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;

        this.updateToSmartDashboard(currentValue);
    }

    /**
     * Full-args constructor with id of configurable setting in SmartDashboard/Shuffleboard and
     * the starting values.
     *
     * @param id Id of the configurable setting in SmartDashboard/Shuffleboard.
     * @param defaultValue The default value to put in SmartDashboard/Shuffleboard.
     * @param value The starting value in robot memory of the setting.
     */
    public SmartDashboardStringSupplier(String id, String defaultValue, String value) {
        super(id);

        this.defaultValue = defaultValue;
        this.currentValue = value;

        this.updateToSmartDashboard(currentValue);
    }

    /**
     * Performs a SmartDashboard/Shuffleboard get and returns the updated number.
     *
     * @return Current value in SmartDashboard/Shuffleboard.
     */
    public String get() {
        this.updateFromSmartDashboard();
        return currentValue;
    }

    /**
     * Sets the value in memory and then performs a SmartDashboard/Shuffleboard set operation.
     *
     * @param value The value to set.
     */
    public void set(String value) {
        this.currentValue = value;
    }

    /**
     * Syncs the value in memory to the value in SmartDashboard/Shuffleboard.
     */
    public void updateFromSmartDashboard() {
        currentValue = SmartDashboard.getString(id, defaultValue);
    }

    /**
     * Syncs the value in SmartDashboard/Shuffleboard with the value in memory.
     *
     * @param value The new value to sync to the SmartDashboard/Shuffleboard.
     */
    public void updateToSmartDashboard(String value) {
        SmartDashboard.putString(id, value);
    }
}
