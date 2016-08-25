package org.waterbear.projects.common.testng;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.waterbear.core.utils.BrowserUtil;

public class ProgressTracker implements IInvokedMethodListener2 {

	protected Logger log = Logger.getLogger(getClass().getSimpleName());
	private long startTime = 0;
	private int executedTestCount = 0;

	public static int totalTestedCount = 0;

	@Override
	public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInvocation(IInvokedMethod invokedMethod,
			ITestResult testResult, ITestContext testContext) {
		if (invokedMethod.isTestMethod()) {
			ITestNGMethod m = invokedMethod.getTestMethod();
			String methodName = m.getConstructorOrMethod().getName();
			String className = m.getTestClass().getRealClass().getSimpleName();
			log.info("========<" + (++executedTestCount) + "> [Begin] "
					+ methodName + "(" + className + ") ========");
			if (startTime == 0) {
				startTime = Calendar.getInstance().getTimeInMillis();
			}
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod invokedMethod,
			ITestResult testResult, ITestContext testContext) {
		if (invokedMethod.isTestMethod()) {
			ITestNGMethod m = invokedMethod.getTestMethod();
			String methodName = m.getConstructorOrMethod().getName();
			String className = m.getTestClass().getRealClass().getSimpleName();

			int status = testResult.getStatus();
			String statusText = "Unknown";
			switch (status) {
			case ITestResult.FAILURE:
				statusText = "Failed";
				break;
			case ITestResult.SUCCESS:
				statusText = "Passed";
				break;
			case ITestResult.SKIP:
				statusText = "Skipped";
				break;
			}

			log.info("========[End] " + methodName + "(" + className + "): "
					+ statusText + " ========");

			long elapsedTime = (Calendar.getInstance().getTimeInMillis() - startTime) / 1000;
			int remainingTestCount = totalTestedCount - executedTestCount;
			long remainingTime = (elapsedTime / executedTestCount)
					* remainingTestCount;
			log.info("******** "
					+ executedTestPercent(executedTestCount, totalTestedCount)
					+ " (" + executedTestCount + "/" + totalTestedCount + ") "
					+ ",elapsed time:" + formatTime(elapsedTime)
					+ ",remaining time:" + formatTime(remainingTime)
					+ " ********");
			if (status == ITestResult.FAILURE) {
				try{
					BrowserUtil.refreshPage();
					log.info("[" + methodName + "] failed - refresh page.");
				}catch(Exception e){
					log.warn("Failed to refresh page.",e);
				}	
			}
		}
	}

	private String executedTestPercent(long executedTestCount,
			long totalTestCount) {
		return Math.round((double) executedTestCount * 100
				/ (double) totalTestCount)
				+ "%";
	}

	private String formatTime(long valueInSeconds) {
		long hours = valueInSeconds / 3600;
		valueInSeconds = valueInSeconds % 3600;

		long minutes = valueInSeconds / 60;
		valueInSeconds = valueInSeconds % 60;

		return toTwoDigitsStr(hours) + ":" + toTwoDigitsStr(minutes) + ":"
				+ toTwoDigitsStr(valueInSeconds);
	}

	private String toTwoDigitsStr(long value) {
		if (value < 10) {
			return "0" + value;
		} else {
			return String.valueOf(value);
		}
	}
}
