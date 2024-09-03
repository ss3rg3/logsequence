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
        System.out.println("├── TEST_ACTION   (" + description + ")");
        while (!this.actions.isEmpty()) {
            Action action = this.actions.poll();
            action.runnable.run();

            switch (action.type) {
                case RUN:
                    break;

                case SLEEP:
                    System.out.println("├── Sleep ──┘");
                    break;

                case TEST:
                    System.out.println("├── Tests OK ");
                    break;

                default:
                    throw new IllegalStateException("ActionType not recognized. That's a bug.");
            }
        }
        System.out.println("└──────────────── (" + this.description + ")");

    }

    public static TestAction create(String description, Function<TestActionBuilder, TestActionBuilder> functionalBuilder) {
        return new TestAction(description, functionalBuilder);
    }

    public static class TestActionBuilder {
        private TestActionBuilder() {
        }

        private final Queue<Action> actions = new LinkedBlockingQueue<>();

        public TestActionBuilder run(Runnable runnable) {
            this.actions.add(new Action(ActionType.RUN, runnable));
            return this;
        }

        public TestActionBuilder sleep(int durationInMs, String message) {
            this.actions.add(new Action(ActionType.SLEEP, () -> {
                try {
                    System.out.println(INDENT + "├── Sleep ──┐     (" + message + ")");
                    Thread.sleep(durationInMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
            return this;
        }

        public TestActionBuilder test(Runnable runnable) {
            this.actions.add(new Action(ActionType.TEST, runnable));
            return this;
        }

    }

    private static class Action {
        final ActionType type;
        final Runnable runnable;

        public Action(ActionType type, Runnable runnable) {
            this.type = type;
            this.runnable = runnable;
        }
    }

    private enum ActionType {
        RUN,
        SLEEP,
        TEST
    }

}
