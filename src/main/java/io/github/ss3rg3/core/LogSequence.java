package io.github.ss3rg3.core;

import java.util.List;

public class LogSequence {

    /**
     * Expects to find the tests in the exact order as given. If a test is not found then the sequence is considered as failed.
     */
    public static SequenceBuilder exactOrder(List<String> logs) {
        return new SequenceBuilder(logs, SequenceMode.EXACT_ORDER);
    }

    /**
     * Expects to find the tests in any arbitrary order. If a test matches with a log line, then this log line will be
     * removed from the corpus and will not be available for the subsequent tests. This avoids false-positives.
     */
    public static SequenceBuilder anyOrder(List<String> logs) {
        return new SequenceBuilder(logs, SequenceMode.ANY_ORDER);
    }

}
