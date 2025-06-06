package io.resttestgen.implementation.strategy;

import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.datatype.parameter.Parameter;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.openapi.Operation;
import io.resttestgen.core.testing.Strategy;
import io.resttestgen.core.testing.TestRunner;
import io.resttestgen.core.testing.TestSequence;
import io.resttestgen.implementation.fuzzer.NominalFuzzer;
import io.resttestgen.implementation.oracle.BlockStatusCodeOracle;
import io.resttestgen.implementation.oracle.StatusCodeOracle;
import io.resttestgen.implementation.writer.ReportWriter;
import io.resttestgen.implementation.writer.RestAssuredWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.resttestgen.implementation.strategy.CredentialStuffingSecurityTestingStrategy.filterLoginOperations;

public class PasswordBruteForceSecurityTestingStrategy extends Strategy {

    private static final Logger logger = LogManager.getLogger(PasswordBruteForceSecurityTestingStrategy.class);
    private static final String STATIC_USERNAME = "danishrandomemail@gmail.com";
    private static final int MAX_LOGIN_ATTEMPTS = 10;

    public void start() {
        TestRunner runner = TestRunner.getInstance();
        runner.removeInvalidStatusCode(new HttpStatusCode(429));

        List<Operation> operations = new ArrayList<>(Environment.getInstance().getOpenAPI().getOperations());
        List<Operation> loginOperations = filterLoginOperations(operations);
        if (loginOperations.isEmpty()) {
            System.out.println("No LOG_IN operations found in the OpenAPI documentation.");
            return;
        }
        for (Operation operation : loginOperations) {
            TestSequence attemptsSequence = new TestSequence();
            for (int j = 0; j < MAX_LOGIN_ATTEMPTS; j++) {
                NominalFuzzer nominalFuzzer = new NominalFuzzer(operation);
                TestSequence sequence = nominalFuzzer.generateTestSequences(1).get(0);
                // Find the user ID parameter
                Parameter userIdParam = findUserIdParameter((List<LeafParameter>) sequence.get(0).getFuzzedOperation().getLeaves());
                LeafParameter secretTokenParam = findSecretTokenParameter((List<LeafParameter>) sequence.get(0).getFuzzedOperation().getLeaves());
                if (userIdParam != null && userIdParam instanceof LeafParameter) {
                    ((LeafParameter) userIdParam).setValueManually(STATIC_USERNAME);
                }
                if (secretTokenParam != null) {
                    secretTokenParam.setValueManually("secretrandomtoken");
                }
                attemptsSequence.append(sequence);
            }
            runner.run(attemptsSequence);
            StatusCodeOracle statusCodeOracle = new StatusCodeOracle();
            statusCodeOracle.assertTestSequence(attemptsSequence);
            BlockStatusCodeOracle blockStatusCodeOracle = new BlockStatusCodeOracle();
            blockStatusCodeOracle.assertTestSequence(attemptsSequence);
            try {
                ReportWriter reportWriter = new ReportWriter(attemptsSequence);
                reportWriter.write();
                RestAssuredWriter restAssuredWriter = new RestAssuredWriter(attemptsSequence);
                restAssuredWriter.write();
            } catch (IOException e) {
                logger.warn("Could not write report to file.");
                e.printStackTrace();
            }
        }
    }

    public static Parameter findUserIdParameter(List<LeafParameter> leaves) {
        LeafParameter bestMatch = null;
        int bestScore = -1; // To hold the highest score
        for (LeafParameter leafParam : leaves) {
            String paramNameLower = leafParam.getName().toString().toLowerCase();
            int score = scoreParameterName(paramNameLower);
            // Update the best match if this parameter has a higher score
            if (score > bestScore) {
                bestScore = score;
                bestMatch = leafParam;
            }
        }
        return bestMatch;
    }
    private static int scoreParameterName(String paramName) {
        if (paramName.contains("username")) return 5;
        if (paramName.contains("email")) return 4;
        if (paramName.contains("login")) return 3;
        if (paramName.contains("userid")) return 2;
        if (paramName.contains("name")) return 1;
        return 0;
    }

    public static LeafParameter findSecretTokenParameter(List<LeafParameter> leaves) {
        for (LeafParameter leafParam : leaves) {
            if (leafParam.getName().toString().toLowerCase().contains("token")) {
                return leafParam;
            }
        }
        return null;
    }
}
