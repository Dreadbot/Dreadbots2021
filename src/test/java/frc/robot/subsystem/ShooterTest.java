package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import frc.robot.subsystem.mock.MockCANSparkMotor;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShooterTest {

    private CANSparkMax shooterMotor;
    private CANSparkMax aimingMotor;
    private Shooter shooter;

    @Before
    public void setup() {
        shooterMotor = new MockCANSparkMotor();
        aimingMotor = new MockCANSparkMotor();
        shooter = new Shooter(shooterMotor, aimingMotor);
    }

    @Test
    public void testShoot() {
        shooter.shoot(500);
        assertEquals(500, shooter.getShootingSpeed());

        shooter.shoot(10);
        assertEquals(10, shooter.getShootingSpeed());
    }

    @Test
    public void testAimHight() {
        shooter.aimHeight(12);
        assertEquals(12, shooter.getHoodPosition());
    }


}
