package frc.robot.utility;

public class DreadbotMath {
	/**
	 * Java Generic Clamp Function
	 * <p>
	 * Clamp functions specify specific constraints for a value's validity. For
	 * example, if you need to ensure that a number is within certain constraints to
	 * avoid error-prone code, use a clamp function.
	 *
	 * @param <T>          Generic Type that extends that of a Comparable object.
	 *                     (Can be compared)
	 * @param testingValue The given value to use against the clamp restraints.
	 * @param clampMinimum The minimum value for the clamp function.
	 * @param clampMaximum The maximum value for the clamp function.
	 * @return The result or output of the clamp function.
	 */
	public static <T extends Comparable<T>> T clampValue(final T testingValue, final T clampMinimum,
	                                                     final T clampMaximum) {
		if (testingValue.compareTo(clampMinimum) < 0)
			return clampMinimum;
		if (testingValue.compareTo(clampMaximum) > 0)
			return clampMaximum;

		return testingValue;
	}

	/**
	 * Java Generic Deadband Processing Function
	 * <p>
	 * A deadband is a band of input values in the domain of a function in a control
	 * system where the output should be zero. Deadband regions are used in control
	 * systems to prevent unecessary undefined behavior due to unwanted control
	 * precision, preventing oscillation or unwanted events.
	 *
	 * @param <T>           Generic Type that extends that of a Comparable object.
	 *                      (Can be compared)
	 * @param testingValue  The given value to use against the deadband constraints.
	 * @param deadbandMin   The minimum value for the deadband function.
	 * @param deadbandMax   The maximum value for the deadband function.
	 * @param deadbandValue The value to return when deadband constraints are met.
	 * @return The result or output of the deadband function.
	 */
	public static <T extends Comparable<T>> T applyDeadbandToValue(final T testingValue, final T deadbandMin,
	                                                               final T deadbandMax, final T deadbandValue) {
		if (testingValue.compareTo(deadbandMin) > 0 && testingValue.compareTo(deadbandMax) < 0)
			return deadbandValue;

		return testingValue;
	}

	/**
	 * Normalizes an array of given values to fit between -1.0 and 1.0.
	 * <p>
	 * Normalization is especially important for drive code, as most
	 * SpeedControllers only accept values between -1.0 and 1.0, since these values
	 * represent percentages of voltage.
	 *
	 * @param values The given values to normalize.
	 * @return The values, now normalized.
	 */
	public static double[] normalizeValues(final double[] values) {
		final double[] absoluteValues = new double[values.length];
		for (int i = 0; i < absoluteValues.length; i++)
			absoluteValues[i] = Math.abs(values[i]);

		final double magnitude = DreadbotMath.maximumElement(absoluteValues);
		if (magnitude > 1.0)
			for (int i = 0; i < values.length; i++)
				values[i] /= magnitude;

		return values;
	}

	/**
	 * Normalizes an array of given values to fit between -1.0f and 1.0f.
	 * <p>
	 * Normalization is especially important for drive code, as most
	 * SpeedControllers only accept values between -1.0 and 1.0, since these values
	 * represent percentages of voltage.
	 *
	 * @param values The given values to normalize.
	 * @return The values, now normalized.
	 */
	public static float[] normalizeValues(final float[] values) {
		final float[] absoluteValues = new float[values.length];
		for (int i = 0; i < absoluteValues.length; i++)
			absoluteValues[i] = Math.abs(values[i]);

		final float magnitude = DreadbotMath.maximumElement(absoluteValues);
		if (magnitude > 1.0)
			for (int i = 0; i < values.length; i++)
				values[i] /= magnitude;

		return values;
	}

	/**
	 * Finds and returns the maximum element in an array of given elements.
	 *
	 * @param values The given values to search.
	 * @return The greatest element in the array.
	 */
	public static double maximumElement(final double[] values) {
		double currentMaximumElement = 0.0;

		for (final double element : values)
			if (element > currentMaximumElement)
				currentMaximumElement = element;

		return currentMaximumElement;
	}

	/**
	 * Finds and returns the maximum element in an array of given elements.
	 *
	 * @param values The given values to search.
	 * @return The greatest element in the array.
	 */
	public static float maximumElement(final float[] values) {
		float currentMaximumElement = 0.0f;

		for (final float element : values)
			if (element > currentMaximumElement)
				currentMaximumElement = element;

		return currentMaximumElement;
	}
}
