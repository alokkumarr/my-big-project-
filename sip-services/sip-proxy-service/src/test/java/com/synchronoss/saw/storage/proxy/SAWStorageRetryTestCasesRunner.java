package com.synchronoss.saw.storage.proxy;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class has been added to make 
 * it sure to retry the beforeTest method.
 * Because the resource are not being released by previous testClass.
 * So on failure it will retry 5 times.
 * @author spau0004
 */
public class SAWStorageRetryTestCasesRunner extends SpringJUnit4ClassRunner {

    private final int retryCount = 5;
    private int failedAttempts = 0;

    public SAWStorageRetryTestCasesRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }    

    @Override
    public void run(final RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        Statement statement = classBlock(notifier);
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            retry(testNotifier, statement, e);
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }

    protected final void runTestUnit(Statement statement, Description description,
            RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            retry(eachNotifier, statement, e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    public void retry(EachTestNotifier notifier, Statement statement, Throwable currentThrowable) {
        Throwable caughtThrowable = currentThrowable;
        while (retryCount > failedAttempts) {
            try {
                statement.evaluate();
                return;
            } catch (Throwable t) {
                failedAttempts++;
                caughtThrowable = t;
            }
        }
        notifier.addFailure(caughtThrowable);
    }
}
