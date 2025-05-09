package io.resttestgen.implementation.strategy;

import io.resttestgen.boot.AuthenticationInfo;
import io.resttestgen.boot.Configuration;
import io.resttestgen.core.Environment;
import io.resttestgen.core.helper.ExtendedRandom;
import io.resttestgen.core.openapi.Operation;
import io.resttestgen.core.testing.Strategy;
import io.resttestgen.core.testing.TestInteraction;
import io.resttestgen.core.testing.TestRunner;
import io.resttestgen.core.testing.TestSequence;
import io.resttestgen.core.testing.operationsorter.OperationsSorter;
import io.resttestgen.implementation.fuzzer.NominalFuzzer;
import io.resttestgen.implementation.operationssorter.GraphBasedOperationsSorter;
import io.resttestgen.implementation.oracle.UncheckedTokenAuthenticityStatusCodeOracle;
import io.resttestgen.implementation.oracle.StatusCodeOracle;
import io.resttestgen.implementation.writer.CoverageReportWriter;
import io.resttestgen.implementation.writer.ReportWriter;
import io.resttestgen.implementation.writer.RestAssuredWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import io.resttestgen.boot.Configuration;

public class UncheckedTokenAuthenticityTestingStrategy extends Strategy {

    private static final Logger logger = LogManager.getLogger(UncheckedTokenAuthenticityTestingStrategy.class);
    private final TestSequence globalNominalTestSequence = new TestSequence();

    private Set<Operation> nominalSuccessfulOperations = new HashSet<>();
    private Set<Operation> noTokenSuccessfulOperations = new HashSet<>();
    private Set<Operation> mutatedTokenSuccessfulOperations = new HashSet<>();
    private Set<Operation> vulnerableOperations = new HashSet<>();
    protected final Configuration configuration = Environment.getInstance().getConfiguration();

    public void start() {

        // According to the order provided by the graph, execute the nominal fuzzer
        OperationsSorter sorter = new GraphBasedOperationsSorter();
        while (!sorter.isEmpty()) {

            Operation operationToTest = sorter.getFirst();
            logger.debug("Testing operation " + operationToTest);


            for (int i = 0; i < 2; i++) {
                NominalFuzzer nominalFuzzer = new NominalFuzzer(operationToTest);
                TestSequence nominalFuzzedSequence = nominalFuzzer.generateTestSequences(1).get(0);

                TestRunner testRunner = TestRunner.getInstance();
                testRunner.run(nominalFuzzedSequence);

                if (nominalFuzzedSequence.isExecuted()) {
                    if (nominalFuzzedSequence.get(0).getResponseStatusCode().isSuccessful()) {
                        globalNominalTestSequence.append(nominalFuzzedSequence);
                        break;
                    }
                }
            }
            sorter.removeFirst();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Keep only successful test interactions in the sequence
        globalNominalTestSequence.filterBySuccessfulStatusCode();

        // Add operations in globalNominalTestSequence to Nominal set
        globalNominalTestSequence.forEach(s -> nominalSuccessfulOperations.add(s.getFuzzedOperation()));

        TestSequence mutatedTokenTestSequence = globalNominalTestSequence.deepClone().reset();

        AuthenticationInfo authenticationInfo = Environment.getInstance().getApiUnderTest().getAuthenticationInfo("default");
        if (authenticationInfo == null) {
            logger.error("Authentication information is not available.");
            return;
        }
        String originalValue = authenticationInfo.getValue();
        if (originalValue == null) {
            logger.error("Original value for the token is not set.");
            return;
        }
        TestRunner runner = TestRunner.getInstance();

        for (TestInteraction testInteraction : mutatedTokenTestSequence) {
            // Mutate the token
            String mutatedValue = mutateValue(originalValue);
            authenticationInfo.setValue(mutatedValue);
            TestSequence temp = new TestSequence();
            temp.add(testInteraction);
            runner.run(temp);
        }

        // Store successful operation of mutated token sequence to the set
        mutatedTokenTestSequence.forEach(s -> mutatedTokenSuccessfulOperations.add(s.getFuzzedOperation()));

        // Remove authentication info
        Environment.getInstance().getApiUnderTest().removeAllAuthenticationInfo();

        // Replay the same sequence but this time remove all tokens (third time)
        TestSequence noTokenTestSequence = globalNominalTestSequence.deepClone().reset();
        runner.run(noTokenTestSequence);

        // Store successful interactions in the set (noTokenSuccessfulOperations)
        noTokenTestSequence.forEach(s -> noTokenSuccessfulOperations.add(s.getFuzzedOperation()));

        // Compute set difference this way = WRONG TOKEN - No Token
        Set<Operation> vulnerableOperations = new HashSet<>(mutatedTokenSuccessfulOperations);
        vulnerableOperations.removeAll(noTokenSuccessfulOperations);

        // Print all the 3 sets and difference in those file.
        writeResultsToFile();
    }

    private void writeResultsToFile() {
        String sessionFolderPath = configuration.getOutputPath() + configuration.getTestingSessionName() + "/";
        String filename = "successful_operations.txt";
        String fullPath = sessionFolderPath + filename;
        try {
            File sessionDir = new File(sessionFolderPath);
            if (!sessionDir.exists()) {
                sessionDir.mkdirs();
            }
            // Write the results to the file
            try (FileWriter writer = new FileWriter(fullPath)) {
                writer.write("Nominal: " + nominalSuccessfulOperations + System.lineSeparator());
                writer.write("Mutated token: " + mutatedTokenSuccessfulOperations + System.lineSeparator());
                writer.write("No token: " + noTokenSuccessfulOperations + System.lineSeparator());
                writer.write("Vulnerable: " + vulnerableOperations + System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("Error writing to file: " + fullPath, e);
        }
    }

    private String mutateValue(String originalValue) {
        ExtendedRandom rand = Environment.getInstance().getRandom();
        int mutationType = rand.nextInt(0, 3);
        int position = rand.nextInt(originalValue.length());
        switch (mutationType) {
            case 0:
                char mutatedChar = originalValue.charAt(position);
                while (mutatedChar == originalValue.charAt(position)) {
                    mutatedChar = getRandomChar();
                }
                return originalValue.substring(0, position) + mutatedChar + originalValue.substring(position + 1);
            case 1:
                return originalValue.substring(0, position) + originalValue.substring(position + 1);
            case 2:
                return originalValue.substring(0, position) + getRandomChar() + originalValue.substring(position);
            case 3:
                return "";
            default:
                return originalValue;
        }
    }
    private char getRandomChar() {
        Random rand = new Random();
        int choice = rand.nextInt(3);

        switch (choice) {
            case 0:
                return (char) (rand.nextInt(10) + '0');
            case 1:
                return (char) (rand.nextInt(26) + 'A');
            case 2:
                return (char) (rand.nextInt(26) + 'a');
            default:
                return 'X';
        }
    }
}