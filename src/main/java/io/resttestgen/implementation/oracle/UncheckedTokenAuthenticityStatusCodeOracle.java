package io.resttestgen.implementation.oracle;

import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.testing.Oracle;
import io.resttestgen.core.testing.TestInteraction;
import io.resttestgen.core.testing.TestResult;
import io.resttestgen.core.testing.TestSequence;

/**
 * Evaluates an erroneous sequence whose last test interaction is mutated by the error fuzzer.
 */
public class UncheckedTokenAuthenticityStatusCodeOracle extends Oracle {

    @Override
    public TestResult assertTestSequence(TestSequence testSequence) {
        TestResult testResult = new TestResult();

        if (!testSequence.isExecuted()) {
            return testResult.setError("One or more interaction in the sequence have not been executed.");
        }
        for (TestInteraction testInteraction : testSequence) {
            HttpStatusCode responseStatusCode = testInteraction.getResponseStatusCode();

            if (responseStatusCode.isSuccessful()) {
                testResult.setFail("The server erroneously accepted a wrong token.");
                break;
            } else if (responseStatusCode.equals(new HttpStatusCode(401)) || responseStatusCode.equals(new HttpStatusCode(403)) || responseStatusCode.equals(new HttpStatusCode(400))) {
                testResult.setPass("The server correctly rejected the unauthorized token request.");
                break;
            }
        }
        if (testResult.isPending()) {
            testResult.setFail("Unexpected server response.");
        }

        testSequence.addTestResult(this, testResult);
        return testResult;
    }

}
