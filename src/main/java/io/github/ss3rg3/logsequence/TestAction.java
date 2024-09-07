package io.github.ss3rg3.logsequence;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class TestAction {

    private static final String INDENT = "";
    private final String description;
    private final Queue<Action> actions;

    public TestAction(String description, Function<TestActionBuilder, TestActionBuilder> functionalBuilder) {
        TestActionBuilder builder = functionalBuilder.apply(new TestActionBuilder());

        this.description = description;
        this.actions = builder.actions;

        System.out.println("┌──────────────────────────────────────────────────");
        System.out.println("├── " + description);
        while (!this.actions.isEmpty()) {
            Action action = this.actions.poll();
            action.runnable.run();

            String message = action.message == null ? "" : " " + action.message;
            switch (action.type) {
                case RUN:
                    System.out.println("├── Run ───────" + message);
                    break;

                case SLEEP:
                    System.out.println("├── Sleep ──┘");
                    break;

                case TEST:
                    System.out.println("├── Tests ─────" + message);
                    break;

                default:
                    throw new IllegalStateException("ActionType not recognized. That's a bug.");
            }
        }
        System.out.println("└──────────────── (" + this.description + ")");

    }

    public static void execute(String description, Function<TestActionBuilder, TestActionBuilder> functionalBuilder) {
        new TestAction(description, functionalBuilder);
    }

    public static class TestActionBuilder {
        private TestActionBuilder() {
        }

        private final Queue<Action> actions = new LinkedBlockingQueue<>();

        public TestActionBuilder run(Runnable runnable) {
            this.actions.add(new Action(ActionType.RUN, runnable));
            return this;
        }

        public TestActionBuilder run(String message, Runnable runnable) {
            this.actions.add(new Action(ActionType.RUN, message, runnable));
            return this;
        }

        public TestActionBuilder sleep(int durationInMs, String message) {
            this.actions.add(new Action(ActionType.SLEEP, () -> {
                try {
                    System.out.println(INDENT + "├── Sleep ──┬──" + (message == null ? "" : " " + message));
                    Thread.sleep(durationInMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
            return this;
        }

        public TestActionBuilder sleep(int durationInMs) {
            return this.sleep(durationInMs, null);
        }

        public TestActionBuilder test(Runnable runnable) {
            this.actions.add(new Action(ActionType.TEST, runnable));
            return this;
        }

        public TestActionBuilder test(String message, Runnable runnable) {
            this.actions.add(new Action(ActionType.TEST, message, runnable));
            return this;
        }

    }

    private static class Action {
        final String message;
        final ActionType type;
        final Runnable runnable;

        public Action(ActionType type, Runnable runnable) {
            this.type = type;
            this.runnable = runnable;
            this.message = null;
        }

        public Action(ActionType type, String message, Runnable runnable) {
            this.type = type;
            this.runnable = runnable;
            this.message = message;
        }
    }

    private enum ActionType {
        RUN,
        SLEEP,
        TEST
    }

}
