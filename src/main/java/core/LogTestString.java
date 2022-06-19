package core;

public class LogTestString implements LogTest {

    private final String exactString;

    public LogTestString(String exactString) {
        this.exactString = exactString;
    }

    public boolean doesMatch(String row) {
        return row.contains(this.exactString);
    }

    @Override
    public String toString() {
        return this.exactString;
    }
}
