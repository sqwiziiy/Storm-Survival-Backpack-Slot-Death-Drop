package com.mentality.stormsurvivalbackpackdeathcompat;

final class BridgeTransaction {
    private BridgeTransaction() {
    }

    static boolean run(ThrowingRunnable nativeHandoff, Runnable clearSlot) {
        try {
            nativeHandoff.run();
            clearSlot.run();
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run();
    }
}
