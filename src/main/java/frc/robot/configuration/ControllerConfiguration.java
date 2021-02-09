package frc.robot.configuration;

import java.util.HashMap;

public class ControllerConfiguration {
	private HashMap<String, Integer> logitechControllerMappings;

	private int maxAxisPortBound;
	private int maxButtonPortBound;

	public int getPort(String inputId) {
		if (!logitechControllerMappings.containsKey(inputId))
			throw new IllegalArgumentException(
				"inputId \"" + inputId + "\" is an invalid identifier for configured controller.");

		return logitechControllerMappings.get(inputId);
	}

	public HashMap<String, Integer> getLogitechControllerMappings() {
		return logitechControllerMappings;
	}

	public void setLogitechControllerMappings(HashMap<String, Integer> logitechControllerMappings) {
		this.logitechControllerMappings = logitechControllerMappings;
	}

	public int getMaxAxisPortBound() {
		return maxAxisPortBound;
	}

	public void setMaxAxisPortBound(int maxAxisPortBound) {
		this.maxAxisPortBound = maxAxisPortBound;
	}

	public int getMaxButtonPortBound() {
		return maxButtonPortBound;
	}

	public void setMaxButtonPortBound(int maxButtonPortBound) {
		this.maxButtonPortBound = maxButtonPortBound;
	}

	@Override
	public String toString() {
		return "ControllerConfiguration{" +
			"logitechControllerMappings=" + logitechControllerMappings +
			", maxAxisPortBound=" + maxAxisPortBound +
			", maxButtonPortBound=" + maxButtonPortBound +
			'}';
	}
}
