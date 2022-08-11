package core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LogSequenceTest {

    private final List<String> logAppenderList = Arrays.asList(
            "INFO - Engine initialized",
            "INFO - Client 2 connected",
            "INFO - Client 1 connected",
            "INFO - Processing started",
            "INFO - Processing finished"
    );

    @Test
    void exactOrder() {
        LogSequence.exactOrder(this.logAppenderList)
                .rgx("Client \\d connected")
                .rgx("Client \\d connected")
                .str("Processing finished")
                .debug()
                .validate();

        assertThrows(IllegalStateException.class, () ->
                LogSequence.exactOrder(this.logAppenderList)
                        .rgx("Client \\d connected")
                        .rgx("Client \\d connected")
                        .rgx("Client \\d connected") // Does not occur in subsequent log lines
                        .str("Processing finished")
                        .debug()
                        .validate()
        );
    }

    @Test
    void anyOrder() {
        LogSequence.anyOrder(this.logAppenderList)
                .rgx("Process.*finished")
                .str("Client 1 connected")
                .str("Client 2 connected")
                .debug()
                .validate();

        assertThrows(IllegalStateException.class, () ->
                LogSequence.anyOrder(this.logAppenderList)
                        .str("Client 1 connected")
                        .str("Client 2 connected")
                        .str("Client 2 connected")  // Previous test matched and log line was removed, therefore duplicate is not found
                        .rgx("Process.*finished")
                        .debug()
                        .validate()
        );
    }
}
