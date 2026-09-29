package com.mentality.stormsurvivalbackpackdeathcompat;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Dependency-free checks for selection, ordering, failure safety, and one-time execution. */
public final class BridgeLogicTest {
    public static void main(String[] args) {
        try {
            assert StormSurvivalBackpackDeathCompat.class.getConstructor().newInstance() != null;
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Fabric main entrypoint must have a public no-argument constructor", exception);
        }

        assert BackSlotSelector.firstMatch(0, ignored -> "backpack", "backpack"::equals) == -1;
        List<String> nonBackpack = List.of("amulet");
        assert BackSlotSelector.firstMatch(nonBackpack.size(), nonBackpack::get, "backpack"::equals) == -1;
        List<String> backpack = List.of("amulet", "backpack");
        assert BackSlotSelector.firstMatch(backpack.size(), backpack::get, "backpack"::equals) == 1;

        AtomicInteger calls = new AtomicInteger();
        AtomicBoolean cleared = new AtomicBoolean();
        assert BridgeTransaction.run(calls::incrementAndGet, () -> cleared.set(true));
        assert calls.get() == 1 && cleared.get();

        cleared.set(false);
        assert !BridgeTransaction.run(() -> { throw new IllegalStateException("simulated native failure"); }, () -> cleared.set(true));
        assert !cleared.get();

        AtomicInteger placementCalls = new AtomicInteger();
        AtomicInteger fallbackCalls = new AtomicInteger();
        AtomicInteger clearCalls = new AtomicInteger();
        assert NativePlacementTransaction.run(true, () -> {
            placementCalls.incrementAndGet();
            return true;
        }, () -> {
            fallbackCalls.incrementAndGet();
            return true;
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 1 && fallbackCalls.get() == 0 && clearCalls.get() == 1;

        placementCalls.set(0);
        fallbackCalls.set(0);
        clearCalls.set(0);
        assert NativePlacementTransaction.run(true, () -> {
            placementCalls.incrementAndGet();
            return false;
        }, () -> {
            fallbackCalls.incrementAndGet();
            return true;
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 1 && fallbackCalls.get() == 1 && clearCalls.get() == 1;

        placementCalls.set(0);
        fallbackCalls.set(0);
        clearCalls.set(0);
        assert !NativePlacementTransaction.run(true, () -> {
            placementCalls.incrementAndGet();
            throw new IllegalStateException("simulated placement failure");
        }, () -> {
            fallbackCalls.incrementAndGet();
            return true;
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 1 && fallbackCalls.get() == 0 && clearCalls.get() == 0;

        placementCalls.set(0);
        fallbackCalls.set(0);
        clearCalls.set(0);
        assert !NativePlacementTransaction.run(true, () -> {
            placementCalls.incrementAndGet();
            return false;
        }, () -> {
            fallbackCalls.incrementAndGet();
            return false;
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 1 && fallbackCalls.get() == 1 && clearCalls.get() == 0;

        placementCalls.set(0);
        fallbackCalls.set(0);
        clearCalls.set(0);
        assert !NativePlacementTransaction.run(true, () -> {
            placementCalls.incrementAndGet();
            return false;
        }, () -> {
            fallbackCalls.incrementAndGet();
            throw new IllegalStateException("simulated fallback failure");
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 1 && fallbackCalls.get() == 1 && clearCalls.get() == 0;

        placementCalls.set(0);
        fallbackCalls.set(0);
        clearCalls.set(0);
        assert NativePlacementTransaction.run(false, () -> {
            placementCalls.incrementAndGet();
            return true;
        }, () -> {
            fallbackCalls.incrementAndGet();
            return true;
        }, clearCalls::incrementAndGet);
        assert placementCalls.get() == 0 && fallbackCalls.get() == 1 && clearCalls.get() == 1;

        System.out.println("Bridge logic checks passed");
    }
}
