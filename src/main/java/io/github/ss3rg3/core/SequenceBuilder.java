package io.github.ss3rg3.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class SequenceBuilder {

    private final SequenceMode mode;
    private final List<String> logLines;
    private final List<LogTest> logTests = Collections.synchronizedList(new ArrayList<>());
    private boolean isDebug = false;

    public SequenceBuilder(List<String> logLines, SequenceMode mode) {
        this.mode = mode;
        this.logLines = new ArrayList<>(logLines);
    }

    /**
     * Turns on debug mode which will print details about successful matches and such. Print is done via System.out
     */
    public SequenceBuilder debug() {
        this.isDebug = true;
        return this;
    }

    /**
     * Compiles given string into a Pattern and uses Matcher.find() for matching (i.e. it tests if a log line contains the given regex).
     * Pattern is compiled with Pattern.CASE_INSENSITIVE
     */
    public SequenceBuilder rgx(String regex) {
        this.logTests.add(new LogTestRegex(Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
        return this;
    }

    /**
     * Uses String.contains() for testing. String must be found as is, i.e. it's case-sensitive
     */
    public SequenceBuilder str(String exactString) {
        this.logTests.add(new LogTestString(exactString));
        return this;
    }

    /**
     * Runs the tests. Throws IllegalStateException if tests fail.
     */
    public void validate() {
        switch (this.mode) {
            case EXACT_ORDER:
                this.validateExactOrder();
                break;
            case ANY_ORDER:
                this.validateAnyOrder();
                break;
            default:
                throw new IllegalArgumentException("Mode not recognized, got " + this.mode);
        }
    }

    private void validateAnyOrder() {
        List<MatchTuple> successfulMatches = new ArrayList<>();
        List<LogTest> failedTests = new ArrayList<>();

        for (LogTest test : logTests) {
            boolean isSuccess = false;

            int currentIndex = 0;
            for (String line : logLines) {
                if (test.doesMatch(line)) {
                    successfulMatches.add(new MatchTuple(line, test));
                    isSuccess = true;
                    break;
                }
                currentIndex++;
            }

            if (isSuccess) {
                logLines.remove(currentIndex);
            } else {
                failedTests.add(test);
            }
        }

        if (this.isDebug) {
            this.printSuccessfulMatches(successfulMatches);
            this.printFailedTests(failedTests);
        }

        if (successfulMatches.size() < this.logTests.size()) {
            throw new IllegalStateException("Failed to find all desired lines " +
                    "(found " + successfulMatches.size() + " out of " + this.logTests.size() + ")");
        }

    }

    private void validateExactOrder() {
        Iterator<String> logs = this.logLines.iterator();
        Iterator<LogTest> tests = this.logTests.iterator();
        List<MatchTuple> successfulMatches = new ArrayList<>();

        int currentIndex = -1;
        while (tests.hasNext()) {
            LogTest test = tests.next();
            currentIndex++;
            while (logs.hasNext()) {
                String line = logs.next();
                if (test.doesMatch(line)) {
                    successfulMatches.add(new MatchTuple(line, test));
                    break;
                }
            }
            if (!logs.hasNext()) {
                break;
            }
        }

        if (this.isDebug) {
            this.printSuccessfulMatches(successfulMatches);
        }

        if (successfulMatches.size() < this.logTests.size()) {
            throw new IllegalStateException("Failed to find all desired lines " +
                    "(found " + successfulMatches.size() + " out of " + this.logTests.size() + "), " +
                    "need:\n" + this.logTests.get(currentIndex));
        }

    }

    private void printSuccessfulMatches(List<MatchTuple> successfulMatches) {
        int maxLength = 0;
        for (MatchTuple tuple : successfulMatches) {
            if (maxLength <= tuple.logTest.length()) {
                maxLength = tuple.logTest.length();
            }
        }

        System.out.println("-------------------\nSuccessful matches:");
        if (successfulMatches.isEmpty()) {
            System.out.println("NONE");
        }
        for (MatchTuple tuple : successfulMatches) {
            System.out.printf("   %-" + maxLength + "s    => %s\n", tuple.logTest, tuple.logLine);
        }
        System.out.println();
    }

    private void printFailedTests(List<LogTest> failedTests) {
        System.out.println("---------------\nFailed tests:");
        if (failedTests.isEmpty()) {
            System.out.println("   NONE");
        }
        for (LogTest test : failedTests) {
            System.out.println("   "+test);
        }
        System.out.println();
    }
}
