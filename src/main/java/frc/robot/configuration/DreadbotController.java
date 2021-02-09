package frc.robot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import frc.robot.utility.DreadbotMath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DreadbotController {
	private static boolean initialized = false;
	private static ControllerConfiguration controllerConfig;

	private Joystick joystick;

	int port;

	public DreadbotController(int joystickPort) {
		this.joystick = new Joystick(joystickPort);
	}

	public static void init() throws IOException {
		if (initialized)
			throw new IllegalStateException("DreadbotController data initialized more than once.");

		// Get file from RoboRIO Deploy Directory (Workspace: src/main/deploy)
		File logitechMappings = new File(Filesystem.getDeployDirectory(), "logitechMappings.json");
		System.out.println(logitechMappings.getAbsolutePath());
		if (!logitechMappings.exists()) {
			throw new FileNotFoundException("logitechMappings.json not found in src/main/deploy directory.");
		}

		// Convert JSON to ControllerConfiguration Object
		ObjectMapper objectMapper = new ObjectMapper();
		controllerConfig = objectMapper.readValue(logitechMappings, ControllerConfiguration.class);
		System.out.println(controllerConfig.toString());

		initialized = true;
	}

	public double getAxisValue(JoystickInput input) {
		port = controllerConfig.getPort(input.inputId);
		if(!DreadbotMath.inRange(port, 0, controllerConfig.getMaxAxisPortBound()))
			throw new IllegalArgumentException("Port value specified is not configured with an associated input.");
		return joystick.getRawAxis(port);
	}

	public boolean isButtonPressed(JoystickInput input) {
		port = controllerConfig.getPort(input.inputId);
		if(!DreadbotMath.inRange(port, 0, controllerConfig.getMaxButtonPortBound()))
			throw new IllegalArgumentException("Port value specified is not configured with an associated input.");
		return joystick.getRawButton(port);
	}

	public enum JoystickInput {
		X_AXIS("xAxis"),
		Y_AXIS("yAxis"),
		Z_AXIS("zAxis"),
		W_AXIS("wAxis"),

		X_BUTTON("xButton"),
		A_BUTTON("aButton"),
		B_BUTTON("bButton"),
		Y_BUTTON("yButton"),

		LEFT_BUMPER("leftBumper"),
		RIGHT_BUMPER("rightBumper"),
		LEFT_TRIGGER("leftTrigger"),
		RIGHT_TRIGGER("rightTrigger"),

		BACK_BUTTON("backButton"),
		START_BUTTON("startButton");

		public final String inputId;

		JoystickInput(String inputId) {
			this.inputId = inputId;
		}
	}

}
