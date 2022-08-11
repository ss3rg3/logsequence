package io.github.ss3rg3.logsequence._testutils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogSequenceAppender extends AppenderBase<ILoggingEvent> {

    public static List<String> logs = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void append(ILoggingEvent event) {
        logs.add(String.format("%s - %s", event.getLevel().toString(), event.getFormattedMessage()));
    }

}
