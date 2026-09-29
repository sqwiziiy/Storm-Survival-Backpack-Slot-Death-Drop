package com.mentality.stormsurvivalbackpackdeathcompat;

/** Keeps the exact-one-result placement/fallback decision independently testable. */
final class NativePlacementTransaction {
    private NativePlacementTransaction() {
    }

    static boolean run(boolean placementEnabled, ThrowingBooleanSupplier placement, ThrowingBooleanSupplier fallback, Runnable clearSlot) {
        try {
            boolean transferred = placementEnabled && placement.getAsBoolean();
            if (!transferred) {
                transferred = fallback.getAsBoolean();
            }
            if (!transferred) {
                return false;
            }
            clearSlot.run();
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    @FunctionalInterface
    interface ThrowingBooleanSupplier {
        boolean getAsBoolean();
    }
}
