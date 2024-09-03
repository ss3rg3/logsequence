package io.github.ss3rg3.logsequence;

import org.junit.jupiter.api.Test;

class TestActionTest {

    @Test
    void run() {

        new TestAction("Manual test which won't fail", _0 -> _0
                .run(() ->
                        System.out.println("Running some stuff"))
                .test(() ->
                        System.out.println("Testing again..."))
                .sleep(100, "Doing some sleep")
                .run(() ->
                        System.out.println("Running some more stuff"))
                .sleep(100, "Doing more sleep")
                .test(() ->
                        System.out.println("Testing again..."))
        );

    }

    @Test
    void to() {
        TestAction.create("Manual test which won't fail", _0 -> _0
                .run(() ->
                        System.out.println("Running some stuff"))
                .test(() ->
                        System.out.println("Testing again..."))
                .sleep(100, "Doing some sleep")
                .run(() ->
                        System.out.println("Running some more stuff"))
                .sleep(100, "Doing more sleep")
                .test(() ->
                        System.out.println("Testing again..."))
        );
    }

}
