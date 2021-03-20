package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartDashboardStringSupplier extends SmartDashboardSupplier {
    private final String defaultValue;

    private String value;

    public SmartDashboardStringSupplier(String id, String defaultValue) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        SmartDashboard.putString(id, value);
    }

    public SmartDashboardStringSupplier(String id, String defaultValue, String value) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = value;

        SmartDashboard.putString(id, value);
    }

    public String get() {
        value = SmartDashboard.getString(id, defaultValue);
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
