package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystem.test.TestSegment;

import java.util.ArrayList;

public abstract class Subsystem {
	protected final String name;

	private final ArrayList<TestSegment> tests;
	private final ArrayList<Double> testDurationsSeconds;
	private final ArrayList<String> testNames;
	int currentTestIndex;

	// Testing only
	private boolean testingCompleted;

	private final Timer timer;

	public Subsystem(String name) {
		this.name = name;
		tests = new ArrayList<>();
		testDurationsSeconds = new ArrayList<>();
		testNames = new ArrayList<>();
		currentTestIndex = 0;

		testingCompleted = false;

		timer = new Timer();
	}

	protected void addTest(TestSegment testSegment, String testName, double testDurationSeconds) {
		tests.add(testSegment);
		testDurationsSeconds.add(testDurationSeconds);
		testNames.add(testName);
	}

	protected void addTest(TestSegment testSegment, double testDurationSeconds) {
		tests.add(testSegment);
		testDurationsSeconds.add(testDurationSeconds);
		testNames.add("");
	}

	public final void testInit() {
		System.out.printf("Starting subsystem %s test routine...%n");
		timer.start();
	}

	public final void testPeriodic() {
		if(currentTestIndex >= tests.size()) {
			testingCompleted = true;
			return;
		}

		double testSeconds = testDurationsSeconds.get(currentTestIndex);
		if(!timer.hasElapsed(testSeconds))
			return;

		System.out.printf("Running Test %s.%s%n", name, testNames.get(currentTestIndex));
		tests.get(currentTestIndex).perform();

		++currentTestIndex;
		timer.stop();
		timer.reset();
		timer.start();
	}
}
