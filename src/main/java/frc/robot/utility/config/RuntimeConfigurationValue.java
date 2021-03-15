package frc.robot.utility.config;

public abstract class RuntimeConfigurationValue {
    protected final String id;

    protected RuntimeConfigurationValue(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
