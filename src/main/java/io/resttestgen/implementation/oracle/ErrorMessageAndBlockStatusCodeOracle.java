package io.resttestgen.implementation.oracle;

import io.resttestgen.core.datatype.parameter.Parameter;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.datatype.parameter.structured.StructuredParameter;
import io.resttestgen.core.testing.TestInteraction;
import io.resttestgen.core.testing.TestResult;
import io.resttestgen.core.testing.TestSequence;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Enhanced Oracle that checks for 429 status code and error message variability over time.
 */
public class ErrorMessageAndBlockStatusCodeOracle extends BlockStatusCodeOracle {

    private static final double LEVENSHTEIN_THRESHOLD = 0.2;
    private static final double JACCARD_THRESHOLD = 0.5; // Adjust threshold for Jaccard distance

    @Override
    public TestResult assertTestSequence(TestSequence testSequence) {
        // Call the original BlockStatusCodeOracle method
        TestResult testResult = super.assertTestSequence(testSequence);

        // If the result is already passed (due to 429), return it directly
        if (testResult.isPass()) {
            return testResult;
        }

        // New functionality: check error message variability
        List<String> errorMessages = new ArrayList<>();

        for (TestInteraction testInteraction : testSequence) {
            // Retrieve error messages from the test interaction using the new method
            errorMessages.addAll(getErrorMessagesFromInteraction(testInteraction));
        }

        // Check if error messages show sufficient variation (either Levenshtein or Jaccard should pass)
        if (!checkErrorMessageSimilarity(errorMessages)) {
            testResult.setFail("The server's error message did not change sufficiently over time.");
        }

        if (testResult.isPending()) {
            testResult.setFail("The server did not block " + testSequence.size() + " wrong login attempts.");
        }

        testSequence.addTestResult(this, testResult);
        return testResult;
    }


    /**
     * Extract error messages from different parts of the response (headers, body, etc.)
     *
     * @param testInteraction The test interaction from which to extract error messages.
     * @return A list of error messages extracted.
     */
    private List<String> getErrorMessagesFromInteraction(TestInteraction testInteraction) {
        List<String> errorMessages = new ArrayList<>();

        // Retrieve the response body and extract error messages recursively
        StructuredParameter responseBody = testInteraction.getFuzzedOperation().getResponseBody();
        if (responseBody != null) {
            // Recursively collect leaf values from the structured parameter
            errorMessages.addAll(extractLeafValues(responseBody));
        }

        return errorMessages;
    }

    // Helper method to recursively extract leaf values from a StructuredParameter
    private List<String> extractLeafValues(Parameter parameter) {
        List<String> leafValues = new ArrayList<>();

        if (parameter instanceof LeafParameter) {
            // If it's a leaf, add its value to the list
            leafValues.add(parameter.getValue().toString());
        } else if (parameter instanceof StructuredParameter) {
            // If it's a structured parameter, recursively get the values of its children
            StructuredParameter structuredParameter = (StructuredParameter) parameter;
            for (Parameter child : structuredParameter.getChildren()) {
                leafValues.addAll(extractLeafValues(child));
            }
        }

        return leafValues;
    }


    /**
     * Compares the error messages using a similarity check (e.g., Levenshtein distance or Jaccard distance).
     * If messages are too similar, it indicates a potential vulnerability.
     *
     * @param errorMessages A list of error messages collected from the test interactions.
     * @return true if messages are too similar, false otherwise.
     */
    private boolean checkErrorMessageSimilarity(List<String> errorMessages) {
        if (errorMessages.isEmpty()) {
            return true; // No error messages to compare
        }
        // Compare error messages in pairs
        for (int i = 0; i < errorMessages.size() - 1; i++) {
            String msg1 = errorMessages.get(i);
            String msg2 = errorMessages.get(i + 1);

            double levenshteinSimilarity = calculateLevenshteinSimilarity(msg1, msg2);
            double jaccardSimilarity = calculateJaccardSimilarity(msg1, msg2);

            // If both similarity measures indicate sufficient similarity, return false
            if (levenshteinSimilarity > LEVENSHTEIN_THRESHOLD && jaccardSimilarity > JACCARD_THRESHOLD) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the Levenshtein distance between two strings and normalizes the result.
     * The closer the value is to 1, the more similar they are,
     * which is suitable for measuring the edit distance of characters.
     * @param msg1 First string.
     * @param msg2 Second string.
     * @return The normalized Levenshtein similarity score (0 to 1).
     */
    private double calculateLevenshteinSimilarity(String msg1, String msg2) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int distance = levenshtein.apply(msg1, msg2);
        int maxLength = Math.max(msg1.length(), msg2.length());
        return 1.0 - (double) distance / maxLength;  // Normalize to a similarity score between 0 and 1
    }

    /**
     * Calculates the Jaccard similarity between two strings based on their word sets.
     * The closer the value is to 1, the more similar it is,
     * which is suitable for measuring the similarity between sets.
     * @param msg1 First string.
     * @param msg2 Second string.
     * @return The Jaccard similarity score (0 to 1).
     */
    private double calculateJaccardSimilarity(String msg1, String msg2) {
        Set<String> set1 = new HashSet<>(List.of(msg1.split("\\s+")));
        Set<String> set2 = new HashSet<>(List.of(msg2.split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
