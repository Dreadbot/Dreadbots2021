package frc.robot.utility;

/**
 * Controller/calculator to adjust sensitivity of otherwise "linear" input systems.
 * <p>
 * This controller/calculator takes a single input, percentageSensitivity,
 * which determines how much sensitivity a system has in comparison to systems
 * without sensitivity control (f(x)=x). A positive value of percentageSensitivity
 * will make the system more sensitive, negative vis versa.
 *
 * @see <a href="https://www.desmos.com/calculator/xsm90bs9ro">Desmos Demonstration</a>
 */
public class InputSensitivityController {
    /**
     * The sensitivity factor supplied
     */
    private double percentageSensitivity;

    /**
     * The internally-calculated exponent that is used in
     * InputSensitivityController.calculate()
     */
    private double sensitivityExponent;

    /**
     * Default constructor with default percentageSensitivity of -40% (-.40)
     */
    public InputSensitivityController() {
        this.percentageSensitivity = -.40d;
        calculateSensitivityExponent();
    }

    /**
     * Constructor with specified percentageSensitivity from -100% to 100%
     * (-1.0 to 1.0)
     *
     * @param percentageSensitivity Determines how much sensitivity the system has
     */
    public InputSensitivityController(double percentageSensitivity) {
        DreadbotMath.clampValue(percentageSensitivity, -1.0, 1.0);
        this.percentageSensitivity = percentageSensitivity;
        calculateSensitivityExponent();
    }

    /**
     * Modifies a given input to adjust the given value to the current
     * sensitivity setting.
     *
     * @param input The input to modify
     * @return The modified input (sensitivity)
     */
    public double calculate(double input) {
        if(input == 0.0d) return 0.0d;

        if(input < 0.0d)
            return -(Math.pow(-input, sensitivityExponent));
        else
            return Math.pow(input, sensitivityExponent);
    }

    /**
     * Internal conversion between the given percentage to the
     * exponent for future calculations.
     */
    private void calculateSensitivityExponent() {
        this.sensitivityExponent = Math.exp(-1.88 * percentageSensitivity);
    }

    /**
     * Getter for the given percentageSensitivity.
     *
     * @return the percentage sensitivity setting
     */
    public double getPercentageSensitivity() {
        return percentageSensitivity;
    }

    /**
     * Setter for the given percentageSensitivity.
     *
     * @param percentageSensitivity the percentage sensitivity
     */
    public void setPercentageSensitivity(double percentageSensitivity) {
        this.percentageSensitivity = percentageSensitivity;
        calculateSensitivityExponent();
    }
}
