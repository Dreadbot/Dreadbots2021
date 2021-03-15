package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RuntimeConfigurationString extends RuntimeConfigurationValue {
    private final String defaultValue;

    private String value;

    public RuntimeConfigurationString(String id, String defaultValue) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        SmartDashboard.putString(id, value);
    }

    public RuntimeConfigurationString(String id, String defaultValue, String value) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = value;

        SmartDashboard.putString(id, value);
    }

    public String getValue() {
        value = SmartDashboard.getString(id, defaultValue);
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
