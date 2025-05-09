package io.resttestgen.implementation.oracle;

import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.testing.Oracle;
import io.resttestgen.core.testing.TestInteraction;
import io.resttestgen.core.testing.TestResult;
import io.resttestgen.core.testing.TestSequence;

/**
 * Evaluates an erroneous sequence whose last test interaction is mutated by the error fuzzer.
 */
public class BlockStatusCodeOracle extends Oracle {

    @Override
    public TestResult assertTestSequence(TestSequence testSequence) {
        TestResult testResult = new TestResult();
        if (!testSequence.isExecuted()) {
            return testResult.setError("One or more interactions in the sequence have not been executed.");
        }

        for (TestInteraction testInteraction : testSequence) {
            HttpStatusCode responseStatusCode = testInteraction.getResponseStatusCode();
            if (responseStatusCode.equals(new HttpStatusCode(429))) {
                testResult.setPass("The server successfully blocked " + testSequence.size() + " wrong login attempts.");
                break;
            }
        }

        if (testResult.isPending()) {
            testResult.setFail("The server did not block " + testSequence.size() + " wrong login attempts.");
        }

        testSequence.addTestResult(this, testResult);
        return testResult;
    }

}
