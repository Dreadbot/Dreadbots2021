package frc.robot.utility.config;

public abstract class SmartDashboardSupplier {
    protected final String id;

    protected SmartDashboardSupplier(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
