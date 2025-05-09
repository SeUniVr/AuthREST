package io.resttestgen.implementation.strategy;

import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.openapi.Operation;
import io.resttestgen.core.testing.Strategy;
import io.resttestgen.core.testing.TestRunner;
import io.resttestgen.core.testing.TestSequence;
import io.resttestgen.implementation.fuzzer.NominalFuzzer;
import io.resttestgen.implementation.oracle.BruteStatusCodeOracle;
import io.resttestgen.implementation.oracle.StatusCodeOracle;
import io.resttestgen.implementation.writer.ReportWriter;
import io.resttestgen.implementation.writer.RestAssuredWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.resttestgen.implementation.strategy.CredentialStuffingSecurityTestingStrategy.filterLoginOperations;
import static io.resttestgen.implementation.strategy.PasswordBruteForceSecurityTestingStrategy.findUserIdParameter;

public class CredentialStuffingStrategy extends Strategy {

    private static final Logger logger = LogManager.getLogger(CredentialStuffingStrategy.class);
    private static final String CREDENTIALS_FILE = "test.txt";

    public void start() {
        TestRunner runner = TestRunner.getInstance();
        runner.removeInvalidStatusCode(new HttpStatusCode(429));

        List<Operation> operations = Environment.getInstance().getOpenAPI()
                .getOperations().stream().collect(Collectors.toList());
        List<Operation> loginOperations = filterLoginOperations(operations);
        if (loginOperations.isEmpty()) {
            System.out.println("No LOG_IN operations found in the OpenAPI documentation.");
            return;
        }
        try (Stream<String> stream = Files.lines(Paths.get(CREDENTIALS_FILE))) {
            stream.forEach(credentials -> {
                String[] parts = credentials.split(":");
                String username = null;
                String password = null;
                String secretToken = null;
                if(parts.length > 1){
                    username = parts[0];
                    password = parts[1];
                }else{
                    secretToken = parts[0];
                }
                for (Operation operation : loginOperations) {
                    TestSequence attemptsSequence = new TestSequence();
                    NominalFuzzer nominalFuzzer = new NominalFuzzer(operation);
                    TestSequence sequence = nominalFuzzer.generateTestSequences(1).get(0);
                    LeafParameter userIdParam = (LeafParameter) findUserIdParameter((List<LeafParameter>) sequence.get(0).getFuzzedOperation().getLeaves());
                    if (userIdParam != null && username != null) {
                        userIdParam.setValue(username);
                    }
                    LeafParameter passwordParam = findPasswordParameter((List<LeafParameter>) sequence.get(0).getFuzzedOperation().getLeaves());
                    if (passwordParam != null && password != null) {
                        passwordParam.setValue(password);
                    }
                    LeafParameter secretTokenParam = findSecretTokenParameter((List<LeafParameter>) sequence.get(0).getFuzzedOperation().getLeaves());
                    if (secretTokenParam != null && secretToken != null) {
                        secretTokenParam.setValue(secretToken);
                    }

                    attemptsSequence.append(sequence);
                    runner.run(attemptsSequence);
                    StatusCodeOracle statusCodeOracle = new StatusCodeOracle();
                    statusCodeOracle.assertTestSequence(attemptsSequence);
                    BruteStatusCodeOracle bruteStatusCodeOracle = new BruteStatusCodeOracle();
                    bruteStatusCodeOracle.assertTestSequence(attemptsSequence);
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
            });
        } catch (IOException e) {
            logger.error("Error reading credentials file.", e);
        }
    }

    public static LeafParameter findPasswordParameter(List<LeafParameter> leaves) {
        for (LeafParameter leafParam : leaves) {
            String paramNameLower = leafParam.getName().toString().toLowerCase();
            if (paramNameLower.contains("password")) {
                return leafParam;
            }
        }
        return null;
    }
    public static LeafParameter findSecretTokenParameter(List<LeafParameter> leaves) {
        for (LeafParameter leafParam : leaves) {
            String paramNameLower = leafParam.getName().toString().toLowerCase();
            if (paramNameLower.contains("token")) {
                return leafParam;
            }
        }
        return null;
    }
}
