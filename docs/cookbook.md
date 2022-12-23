{:toc}

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



## Using TestAction

This helper adds additional output via `System.out.println()` which makes reading the logs easier. Works also well with Quarkus and file based logs.

```java
new TestAction("Manual test which won't fail", _0 -> _0
        .run(() ->
                System.out.println("Running some stuff"))
        .sleep(100, "Doing some sleep")
        .test(() ->
                System.out.println("Testing..."))
);
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

  

# Using in Quarkus

As of now, Quarkus doesn't seem to be able to have a custom log appender (see [here](https://stackoverflow.com/questions/73035372/how-to-use-a-custom-log-appender-in-quarkus)). You therefore have to read the logs from a file. This works fine has long as the logs aren't written async but immediately flushed. Example config:

- `application.yml`:

  ```yaml
  "%test":
    quarkus:
      log:
        level: INFO
        category:
          "com.your.tested.package":
            level: DEBUG
        file:
          enable: true
          path: ./target/quarkus.log
        console:
          format: "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p - %s%e [%c{3.}]%n"
  ```

- You might want to set up a `QuarkusTestProfile` to overwrite the log file name:

  ```java
  public class ManualTestProfile implements QuarkusTestProfile {
  
      public final static String LOG_FILE_PATH = "./target/full_lifecycle_test.log";
  
      @Override
      public Map<String, String> getConfigOverrides() {
          return Collections.singletonMap("quarkus.log.file.path", LOG_FILE_PATH);
      }
  
      public static List<String> getLogs() {
          return LogUtils.getLogs(ManualTestProfile.LOG_FILE_PATH);
      }
  
  }
  ```

  And use it in your test class:

  ```java
  @QuarkusTest
  @TestProfile(ManualTestProfile.class)
  public class FullLifeCycleTest {
  ```

  
