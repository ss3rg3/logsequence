package io.github.ss3rg3.logsequence;

public class MatchTuple {

    public final String logLine;
    public final String logTest;

    public MatchTuple(String logLine, LogTest logTest) {
        this.logLine = logLine;
        this.logTest = logTest.toString();
    }
}
