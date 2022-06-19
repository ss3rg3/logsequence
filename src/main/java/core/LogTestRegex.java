package core;

import java.util.regex.Pattern;

public class LogTestRegex implements LogTest {

    private final Pattern pattern;

    public LogTestRegex(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean doesMatch(String row) {
        return pattern.matcher(row).find();
    }

    @Override
    public String toString() {
        return String.valueOf(pattern);
    }
}
