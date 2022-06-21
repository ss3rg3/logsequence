# Understanding LogSequence

LogSequence allows you test sequences of logs. You provide a list of strings (e.g. from a custom log appender) and define a sequence of strings you expect. 

```java
// YOUR LOGS
List<String> logAppenderList = List.of(
    "INFO - Engine initialized",
    "INFO - Client 2 connected",
    "INFO - Client 1 connected",
    "INFO - Processing started",
    "INFO - Processing finished"
);

// EXACT ORDER
LogSequence.exactOrder(logAppenderList) // Must be in the EXACT order
    .rgx("Client \\d connected") // Regex matching, if string occurs in a log line
    .rgx("Client \\d connected")
    .str("Processing finished")	 // Exact string matching, if string occurs in a log line
    .debug() // Prints debug info to stdout
    .validate(); // Execute

// ANY ORDER
LogSequence.anyOrder(logAppenderList) // Can be in ANY order
        .rgx("Process.*finished")
        .str("Client 1 connected")
        .str("Client 2 connected")
        .str("Client 2 connected") // This will fail because line doesn't occur twice
        .debug()
        .validate();
```



# Logback Appender

- Create a simple appender. Note that the logs are a simple `public static List<String>`, so you can access them via `LogSequenceAppender.logs`

  ```java
  public class LogSequenceAppender extends AppenderBase<ILoggingEvent> {
  
      public static List<String> logs = Collections.synchronizedList(new ArrayList<>());
  
      @Override
      protected void append(ILoggingEvent event) {
          logs.add(String.format("%s - %s", event.getLevel().toString(), event.getFormattedMessage()));
      }
  
  }
  ```

- Add to your `logback-test.xml` (adjust `class=""` to your classpath)

  ```xml
  <appender name="LOG_SEQUENCE" class="_testutils.LogSequenceAppender" />
  
  <root level="debug">
      <appender-ref ref="LOG_SEQUENCE" />
  </root>
  ```

  

