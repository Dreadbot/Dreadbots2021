package frc.robot.utility.supplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartDashboardBooleanSupplier extends SmartDashboardSupplier {
    /**
     * Default or starting value on SmartDashbaord/Shuffleboard.
     */
    private final boolean defaultValue;

    /**
     * The current value in memory since the last sync with SmartDashbaord/Shuffleboard
     */
    private boolean currentValue;

    /**
     * Constructor with id of configurable setting in SmartDashboard/Shuffleboard and default value.
     *
     * @param id Id of the configurable setting in SmartDashboard/Shuffleboard.
     * @param defaultValue The default value to put in SmartDashboard/Shuffleboard.
     */
    public SmartDashboardBooleanSupplier(String id, boolean defaultValue) {
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
    public SmartDashboardBooleanSupplier(String id, boolean defaultValue, boolean value) {
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
    public boolean get() {
        this.updateFromSmartDashboard();
        return currentValue;
    }

    /**
     * Sets the value in memory and then performs a SmartDashboard/Shuffleboard set operation.
     *
     * @param value The value to set.
     */
    public void set(boolean value) {
        this.currentValue = value;
        this.updateToSmartDashboard(value);
    }

    /**
     * Syncs the value in memory to the value in SmartDashboard/Shuffleboard.
     */
    public void updateFromSmartDashboard() {
        currentValue = SmartDashboard.getBoolean(id, defaultValue);
    }

    /**
     * Syncs the value in SmartDashboard/Shuffleboard with the value in memory.
     *
     * @param value The new value to sync to the SmartDashboard/Shuffleboard.
     */
    public void updateToSmartDashboard(boolean value) {
        SmartDashboard.putBoolean(id, value);
    }
}
