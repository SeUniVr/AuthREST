package io.resttestgen.implementation.oracle;

import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.testing.Oracle;
import io.resttestgen.core.testing.TestInteraction;
import io.resttestgen.core.testing.TestResult;
import io.resttestgen.core.testing.TestSequence;

/**
 * Evaluates an erroneous sequence whose last test interaction is mutated by the error fuzzer.
 */
public class BruteStatusCodeOracle extends Oracle {

    @Override
    public TestResult assertTestSequence(TestSequence testSequence) {

        TestResult testResult = new TestResult();

        if (!testSequence.isExecuted()) {
            return testResult.setError("One or more interaction in the sequence have not been executed.");
        }

        int successCount = 0;
        int totalCount = 0;

        for (TestInteraction testInteraction : testSequence) {
            if (testInteraction.getResponseStatusCode().isSuccessful()) {
                if (!(testInteraction.getRequestBody().contains("error") ||
                        testInteraction.getRequestBody().contains("unregistered"))) {
                    successCount++;
                }
            }
            totalCount++;
        }
        if (successCount > 0) {
            testResult.setFail("We could login " + successCount + " times out of " + totalCount + ".");
        } else {
            testResult.setPass("No successful login attempts detected.");
        }
        testSequence.addTestResult(this, testResult);
        return testResult;
    }
}
