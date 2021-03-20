package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Supplier to a number configurable on the SmartDashboard/Shuffleboard on the drive computer.
 */
public class SmartDashboardNumberSupplier extends SmartDashboardSupplier {
    /**
     * Default or starting value on SmartDashbaord/Shuffleboard.
     */
    private final double defaultValue;

    /**
     * The current value in memory since the last sync with SmartDashbaord/Shuffleboard
     */
    private double currentValue;

    /**
     * Constructor with id of configurable setting in SmartDashboard/Shuffleboard and default value.
     *
     * @param id Id of the configurable setting in SmartDashboard/Shuffleboard.
     * @param defaultValue The default value to put in SmartDashboard/Shuffleboard.
     */
    public SmartDashboardNumberSupplier(String id, double defaultValue) {
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
    public SmartDashboardNumberSupplier(String id, double defaultValue, double value) {
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
    public double get() {
        this.updateFromSmartDashboard();
        return currentValue;
    }

    /**
     * Sets the value in memory and then performs a SmartDashboard/Shuffleboard set operation.
     *
     * @param value The value to set.
     */
    public void set(double value) {
        this.currentValue = value;
        this.updateToSmartDashboard(value);
    }

    /**
     * Syncs the value in memory to the value in SmartDashboard/Shuffleboard.
     */
    public void updateFromSmartDashboard() {
        currentValue = SmartDashboard.getNumber(id, defaultValue);
    }

    /**
     * Syncs the value in SmartDashboard/Shuffleboard with the value in memory.
     *
     * @param value The new value to sync to the SmartDashboard/Shuffleboard.
     */
    public void updateToSmartDashboard(double value) {
        SmartDashboard.putNumber(id, value);
    }
}
