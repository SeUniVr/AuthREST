package io.resttestgen.implementation.fuzzer;

import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.testing.*;
import io.resttestgen.core.testing.mutator.ParameterMutator;
import io.resttestgen.implementation.mutator.parameter.ConstraintViolationParameterMutator;
import io.resttestgen.implementation.mutator.parameter.MissingRequiredParameterMutator;
import io.resttestgen.implementation.mutator.parameter.WrongTypeParameterMutator;
import io.resttestgen.implementation.oracle.ErrorStatusCodeOracle;
import io.resttestgen.implementation.writer.ReportWriter;
import io.resttestgen.implementation.writer.RestAssuredWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.alg.util.Pair;

import java.io.IOException;
import java.util.*;

public class TokenFuzzer extends Fuzzer {

    private static final Logger logger = LogManager.getLogger(TokenFuzzer.class);

    private final TestSequence testSequenceToMutate;
    private final Set<ParameterMutator> mutators;

    public TokenFuzzer(TestSequence testSequenceToMutate) {
        this.testSequenceToMutate = testSequenceToMutate;
        mutators = new HashSet<>();
        mutators.add(new MissingRequiredParameterMutator());
        mutators.add(new WrongTypeParameterMutator());
        mutators.add(new ConstraintViolationParameterMutator());
    }

    public List<TestSequence> generateTestSequences(int numberOfSequences) {

        List<TestSequence> testSequences = new LinkedList<>();

        // Iterate on interaction of test sequence
        for (TestInteraction interaction : testSequenceToMutate) {

            // For each sequence, we generate n mutants for the last interaction
            for (int j = 0; j < numberOfSequences; j++) {

                // Get clone of the subsequence
                TestSequence currentTestSequence = new TestSequence(this, interaction.deepClone());

                // Get last interaction in the sequence
                TestInteraction mutableInteraction = currentTestSequence.getLast();
                mutableInteraction.addTag("mutated");

                String sequenceName = mutableInteraction.getFuzzedOperation().getOperationId().length() > 0 ?
                        mutableInteraction.getFuzzedOperation().getOperationId() :
                        mutableInteraction.getFuzzedOperation().getMethod().toString() + "-" +
                                mutableInteraction.getFuzzedOperation().getEndpoint();
                currentTestSequence.setName(sequenceName);
                currentTestSequence.appendGeneratedAtTimestampToSequenceName();

                // Get set of applicable mutations to this operation
                Set<Pair<LeafParameter, ParameterMutator>> mutableParameters = new HashSet<>();
                mutableInteraction.getFuzzedOperation().getLeaves().forEach(leaf -> mutators.forEach(mutator -> {
                        if (mutator.isParameterMutable(leaf)) {
                            mutableParameters.add(new Pair<>(leaf, mutator));
                        }
                }));

                // Choose a random mutation pair
                Optional<Pair<LeafParameter, ParameterMutator>> mutable = Environment.getInstance().getRandom().nextElement(mutableParameters);
                mutable.ifPresent(parameterMutatorPair -> {
                    // Apply mutation
                    LeafParameter parameterToMutate = parameterMutatorPair.getFirst();
                    ParameterMutator mutator = parameterMutatorPair.getSecond();
                    LeafParameter mutated = (LeafParameter) mutator.mutate(parameterToMutate);
                    mutated.addTag("mutated");

                    // Replace the original parameter with mutated one
                    if (mutable.get().getFirst().replace(mutated)) {
                        logger.debug("Mutation applied correctly.");
                    } else {
                        logger.warn("Could not apply mutation.");
                    }

                    // Execute test sequence
                    TestRunner testRunner = TestRunner.getInstance();
                    testRunner.run(currentTestSequence);

                    // Evaluate sequence with oracles
                    ErrorStatusCodeOracle errorStatusCodeOracle = new ErrorStatusCodeOracle();
                    errorStatusCodeOracle.assertTestSequence(currentTestSequence);

                    // Write report to file
                    try {
                        ReportWriter reportWriter = new ReportWriter(currentTestSequence);
                        reportWriter.write();
                        RestAssuredWriter restAssuredWriter = new RestAssuredWriter(currentTestSequence);
                        restAssuredWriter.write();
                    } catch (IOException e) {
                        logger.warn("Could not write report to file.");
                    }

                    testSequences.add(currentTestSequence);
                });
            }
        }

        // Return the list of test sequences
        return testSequences;
    }
}
