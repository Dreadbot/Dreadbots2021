package frc.robot.utility;

public abstract class RuntimeConfigurationValue {
    private final String id;

    protected RuntimeConfigurationValue(String id) {
        this.id = id;
    }

    public abstract void update();

    public String getId() {
        return id;
    }
}
