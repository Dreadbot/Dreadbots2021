package frc.robot.utility.config;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RuntimeConfigurationNumber extends RuntimeConfigurationValue {
    private final double defaultValue;

    private double value;

    public RuntimeConfigurationNumber(String id, double defaultValue) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = defaultValue;

        SmartDashboard.putNumber(id, value);
    }

    public RuntimeConfigurationNumber(String id, double defaultValue, double value) {
        super(id);

        this.defaultValue = defaultValue;
        this.value = value;

        SmartDashboard.putNumber(id, value);
    }

    public double getValue() {
        value = SmartDashboard.getNumber(id, defaultValue);
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
