package io.github.ss3rg3.logsequence;

import io.github.ss3rg3.logsequence._testutils.LogSequenceAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogbackTest {

    /**
     * See logback-test.xml for configuration
     */
    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    static {
        logger.debug("Engine started");
        logger.debug("Client {} connected", 1);
        logger.debug("Client {} connected", 2);
        logger.debug("Processing started");
        logger.info("Processing finished");
    }

    @Test
    void exactOrder() {
        assertEquals(5, LogSequenceAppender.logs.size());

        LogSequence.exactOrder(LogSequenceAppender.logs)
                .rgx("Client \\d connected")
                .rgx("Client \\d connected")
                .str("Processing finished")
                .debug()
                .validate();

        assertThrows(IllegalStateException.class, () ->
                LogSequence.exactOrder(LogSequenceAppender.logs)
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
        LogSequence.anyOrder(LogSequenceAppender.logs)
                .rgx("Process.*finished")
                .str("Client 1 connected")
                .str("Client 2 connected")
                .debug()
                .validate();

        assertThrows(IllegalStateException.class, () ->
                LogSequence.anyOrder(LogSequenceAppender.logs)
                        .str("Client 1 connected")
                        .str("Client 2 connected")
                        .str("Client 2 connected")  // Previous test matched and log line was removed, therefore duplicate is not found
                        .rgx("Process.*finished")
                        .debug()
                        .validate()
        );
    }
}
