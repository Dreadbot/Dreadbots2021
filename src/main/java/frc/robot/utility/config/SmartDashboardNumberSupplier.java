package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartDashboardNumberSupplier extends SmartDashboardSupplier {
    private final double defaultValue;

    private double value;

    public SmartDashboardNumberSupplier(String id, double defaultValue) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        SmartDashboard.putNumber(id, value);
    }

    public SmartDashboardNumberSupplier(String id, double defaultValue, double value) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = value;

        SmartDashboard.putNumber(id, value);
    }

    public double get() {
        value = SmartDashboard.getNumber(id, defaultValue);
        return value;
    }

    public void set(double value) {
        this.value = value;
    }
}
